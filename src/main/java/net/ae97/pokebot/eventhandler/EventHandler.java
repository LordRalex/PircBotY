package net.ae97.pokebot.eventhandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.api.ListenerAdapter;
import net.ae97.pircboty.api.events.CommandEvent;
import net.ae97.pircboty.generics.GenericChannelEvent;
import net.ae97.pircboty.generics.GenericMessageEvent;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.CommandExecutor;
import net.ae97.pokebot.api.EventExecutor;
import net.ae97.pokebot.api.Listener;
import net.ae97.pokebot.api.Priority;
import net.ae97.pokebot.permissions.PermissionEvent;

public final class EventHandler extends ListenerAdapter {

    private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
    private final EventRunner runner;
    private final List<CommandPrefix> commandChars = new ArrayList<>();
    private final PircBotY masterBot;
    private final ExecutorService execServ;
    private final Set<Class<? extends Event>> eventClasses = new HashSet<>();
    private final Map<Class<? extends Event>, Set<EventHandler.EventExecutorService>> eventExecutors = new ConcurrentHashMap<>();
    private final Set<CommandExecutor> commandExecutors = new HashSet<>();

    public EventHandler(PircBotY bot) {
        super();
        masterBot = bot;
        runner = new EventRunner();
        runner.setName("Event_Runner_Thread");
        execServ = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void load() {
        List<String> settings = PokeBot.getSettings().getStringList("command-prefix");
        commandChars.clear();
        if (settings.isEmpty()) {
            settings.add("**");
        }
        for (String commandChar : settings) {
            String[] args = commandChar.split("\\|");
            String prefix = args[0];
            String owner;
            if (args.length == 1) {
                owner = null;
            } else {
                owner = args[1];
            }
            PokeBot.getLogger().log(Level.INFO, "Adding command prefix: " + prefix + (owner == null ? "" : " ( " + owner + ")"));
            commandChars.add(new CommandPrefix(prefix, owner));
        }
        eventExecutors.clear();
        commandExecutors.clear();
        eventClasses.clear();
    }

    public void unload() {
        eventExecutors.clear();
        commandExecutors.clear();
        eventClasses.clear();
    }

    public void registerEvent(Class<? extends Event> cl) {
        eventClasses.add(cl);
        eventExecutors.put(cl, new HashSet<EventExecutorService>());
    }

    public void registerListener(Listener list) {
        PokeBot.getLogger().log(Level.INFO, "  Added listener: " + list.getClass().getName());
        Method[] methods = list.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventExecutor.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) {
                    continue;
                }
                for (Class<? extends Event> clz : eventClasses) {
                    if (clz.equals(params[0])) {
                        eventExecutors.get(clz).add(new EventHandler.EventExecutorService(list, method, method.getAnnotation(EventExecutor.class).priority()));
                        PokeBot.getLogger().log(Level.INFO, "    Registered event: " + clz.getName() + "(" + method.getAnnotation(EventExecutor.class).priority().toString() + ")");
                    }
                }
            }
        }
    }

    public void registerCommandExecutor(CommandExecutor executor) {
        PokeBot.getLogger().log(Level.INFO, "  Added command executor: " + executor.getClass().getName());
        commandExecutors.add(executor);
    }

    public Set<Class<? extends Event>> getEventClasses() {
        return eventClasses;
    }

    public void startQueue() {
        if (!runner.isAlive()) {
            runner.start();
        }
    }

    @Override
    public void onEvent(Event event) {
        queue.add(event);
        if (event instanceof GenericMessageEvent) {
            GenericMessageEvent messageEvent = (GenericMessageEvent) event;
            for (CommandPrefix prefix : commandChars) {
                if (messageEvent.getMessage().startsWith(prefix.getPrefix())) {
                    if (event instanceof GenericChannelEvent) {
                        if (prefix.getOwner() != null & !prefix.getOwner().isEmpty() && !((GenericChannelEvent) event).getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(prefix.getOwner()))) {
                            CommandEvent cmdEvent = new CommandEvent(event.getBot(), messageEvent);
                            queue.add(cmdEvent);
                            break;
                        }
                    }
                }
            }
        }
        runner.ping();
    }

    public void stopRunner() {
        synchronized (runner) {
            runner.interrupt();
        }
        synchronized (eventExecutors) {
            eventExecutors.clear();
        }
    }

    public List<String> getCommandPrefixes() {
        List<String> clone = new ArrayList<>(commandChars.size());
        for (CommandPrefix prefix : commandChars) {
            clone.add(prefix.getPrefix());
        }
        return clone;
    }

    private class EventRunner extends Thread {

        @Override
        public void run() {
            boolean run = true;
            while (run) {
                Event next = queue.poll();
                if (next == null) {
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException ex) {
                            run = false;
                        }
                    }
                    continue;
                }
                if (next instanceof PermissionEvent) {
                    PokeBot.getPermManager().runPermissionEvent((PermissionEvent) next);
                } else if (next instanceof CommandEvent) {
                    CommandEvent evt = (CommandEvent) next;
                    User user = evt.getUser();
                    Channel chan = evt.getChannel();
                    PermissionEvent permEvent = new PermissionEvent(masterBot, user);
                    try {
                        PokeBot.getPermManager().runPermissionEvent(permEvent);
                    } catch (Exception e) {
                        PokeBot.getLogger().log(Level.SEVERE, "Error on permission event", e);
                        continue;
                    }
                    if (evt.getCommand().equalsIgnoreCase("reload")) {
                        User sender = evt.getUser();
                        if (sender != null) {
                            if (!sender.hasPermission((String) null, "bot.reload")) {
                                continue;
                            }
                        }
                        PokeBot.getLogger().log(Level.INFO, "Performing a reload, please hold");
                        if (sender != null) {
                            sender.send().notice("Reloading");
                        }
                        PokeBot.getExtensionManager().unload();
                        PokeBot.getEventHandler().unload();
                        PokeBot.getEventHandler().load();
                        PokeBot.getExtensionManager().load();
                        try {
                            PokeBot.getPermManager().reload();
                            PokeBot.getLogger().log(Level.INFO, "Reloaded permissions");
                            if (sender != null) {
                                sender.send().notice("Reloaded permissions");
                            }
                        } catch (IOException e) {
                            PokeBot.getLogger().log(Level.SEVERE, "Error on reloading permissions", e);
                            if (sender != null) {
                                sender.send().notice("Reloading permissions encountered an error: " + e.getMessage());
                            }
                        }
                        PokeBot.getLogger().log(Level.INFO, "Reloaded");
                        if (sender != null) {
                            sender.send().notice("Reloaded");
                        }
                    } else if (evt.getCommand().equalsIgnoreCase("permreload")) {
                        User sender = evt.getUser();
                        if (sender != null) {
                            if (!sender.hasPermission((String) null, "bot.permreload")) {
                                continue;
                            }
                        }
                        PokeBot.getLogger().log(Level.INFO, "Performing a permission reload, please hold");
                        if (sender != null) {
                            sender.send().notice("Reloading permissions");
                        }
                        try {
                            PokeBot.getPermManager().reload();
                            PokeBot.getLogger().log(Level.INFO, "Reloaded permissions");
                            if (sender != null) {
                                sender.send().notice("Reloaded permissions");
                            }
                        } catch (IOException e) {
                            PokeBot.getLogger().log(Level.SEVERE, "Error on reloading permissions", e);
                            if (sender != null) {
                                sender.send().notice("Reloading permissions encountered an error: " + e.getMessage());
                            }
                        }
                    } else if (evt.getCommand().equalsIgnoreCase("permcache")) {
                        User sender = evt.getUser();
                        if (sender != null) {
                            if (!sender.hasPermission((String) null, "bot.permcache")) {
                                continue;
                            }
                        }
                        if (evt.getArgs().length == 0) {
                            continue;
                        }
                        for (String arg : evt.getArgs()) {
                            PokeBot.getLogger().info("Forcing cache update on " + arg);
                            PermissionEvent p = new PermissionEvent(masterBot, masterBot.getUserChannelDao().getUser(arg), true);
                            PokeBot.getPermManager().runPermissionEvent(p);
                        }
                    } else {
                        for (CommandExecutor exec : commandExecutors) {
                            if (Arrays.asList(exec.getAliases()).contains(evt.getCommand())) {
                                execServ.submit(new CommandRunnable(exec, evt));
                                break;
                            }
                        }
                    }
                } else {
                    Set<EventExecutorService> executors = eventExecutors.get(next.getClass());
                    if (executors == null) {
                        continue;
                    }
                    for (Priority prio : Priority.values()) {
                        for (EventExecutorService exec : executors) {
                            if (exec.getPriority() == prio) {
                                try {
                                    exec.getMethod().invoke(exec.getListener(), next);
                                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                    PokeBot.getLogger().log(Level.SEVERE, "Error on handling " + next.getClass().getName() + " in " + exec.getListener().getClass().getName(), e);
                                }
                            }
                        }
                    }
                }
            }
            PokeBot.getLogger().log(Level.INFO, "Ending event listener");
        }

        public void ping() {
            try {
                synchronized (this) {
                    if (this.isAlive()) {
                        this.notifyAll();
                    } else {
                    }
                }
            } catch (IllegalMonitorStateException e) {
                PokeBot.getLogger().log(Level.SEVERE, "Major issue on pinging event system", e);
            }
        }
    }

    private class CommandRunnable implements Runnable {

        private final CommandExecutor listener;
        private final CommandEvent event;

        public CommandRunnable(CommandExecutor list, CommandEvent evt) {
            listener = list;
            event = evt;
        }

        @Override
        public void run() {
            try {
                listener.runEvent(event);
            } catch (Exception e) {
                PokeBot.getLogger().log(Level.SEVERE, "Error on executing command " + event.getCommand(), e);
            }
        }
    }

    private class CommandPrefix {

        private final String prefix;
        private final String owner;

        public CommandPrefix(String p, String o) {
            prefix = p;
            owner = o;
        }

        public CommandPrefix(String p) {
            this(p, null);
        }

        public String getPrefix() {
            return prefix;
        }

        public String getOwner() {
            return owner;
        }
    }

    private class EventExecutorService {

        private final Method method;
        private final Listener listener;
        private final Priority priority;
        private final boolean ignoreCancelled;

        public EventExecutorService(Listener l, Method m, Priority p) {
            this(l, m, p, false);
        }

        public EventExecutorService(Listener l, Method m, Priority p, boolean ignore) {
            method = m;
            listener = l;
            priority = p;
            ignoreCancelled = ignore;
        }

        public Listener getListener() {
            return listener;
        }

        public Priority getPriority() {
            return priority;
        }

        public Method getMethod() {
            return method;
        }

        public boolean ignoreCancelled() {
            return ignoreCancelled;
        }
    }
}

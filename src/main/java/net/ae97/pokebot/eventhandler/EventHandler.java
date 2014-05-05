package net.ae97.pokebot.eventhandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.api.ListenerAdapter;
import net.ae97.pircboty.api.events.CommandEvent;
import net.ae97.pircboty.generics.GenericChannelEvent;
import net.ae97.pircboty.generics.GenericMessageEvent;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.CommandExecutor;
import net.ae97.pokebot.api.EventExecutor;
import net.ae97.pokebot.api.Listener;
import net.ae97.pokebot.logger.PrefixLogger;

public final class EventHandler extends ListenerAdapter {

    private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();
    private final EventExecutorThread runner;
    private final List<CommandPrefix> commandChars = new ArrayList<>();
    private final PircBotY masterBot;
    private final ExecutorService execServ;
    private final Map<Class<? extends Event>, Set<EventExecutorService>> eventExecutors = new ConcurrentHashMap<>();
    private final Set<CommandExecutor> commandExecutors = new HashSet<>();
    private final Logger logger;

    public EventHandler(PircBotY bot) {
        super();
        logger = new PrefixLogger("EventHandler", PokeBot.getLogger());
        masterBot = bot;
        runner = new EventExecutorThread(this);
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
            String owner = args.length == 2 ? args[1] : null;
            logger.log(Level.INFO, "Adding command prefix: " + prefix + (owner == null ? "" : " (" + owner + ")"));
            commandChars.add(new CommandPrefix(prefix, owner));
        }
        eventExecutors.clear();
        commandExecutors.clear();
    }

    public void unload() {
        eventExecutors.clear();
        commandExecutors.clear();
    }

    public void registerEvent(Class<? extends Event> cl) {
        eventExecutors.put(cl, new HashSet<EventExecutorService>());
    }

    public void registerListener(Listener list) {
        logger.log(Level.INFO, "  Added listener: " + list.getClass().getName());
        Method[] methods = list.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventExecutor.class)) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1) {
                    continue;
                }
                Set<EventExecutorService> services = eventExecutors.get(params[0]);
                if (services == null) {
                    services = new HashSet<>();
                    eventExecutors.put((Class<? extends Event>) params[0], services);
                }
                services.add(new EventExecutorService(list, method, method.getAnnotation(EventExecutor.class).priority()));
                logger.log(Level.INFO, "    Registered event: " + params[0].getName() + "(" + method.getAnnotation(EventExecutor.class).priority().toString() + ")");

            }
        }
    }

    public void registerCommandExecutor(CommandExecutor executor) {
        logger.log(Level.INFO, "  Added command executor: " + executor.getClass().getName());
        commandExecutors.add(executor);
    }

    public void startQueue() {
        runner.start();
    }

    @Override
    public void onEvent(Event event) {
        queue.add(event);
        if (event instanceof GenericMessageEvent) {
            GenericMessageEvent messageEvent = (GenericMessageEvent) event;
            for (CommandPrefix prefix : commandChars) {
                if (messageEvent.getMessage().startsWith(prefix.getPrefix())) {
                    if (event instanceof GenericChannelEvent) {
                        if (prefix.getOwner() != null && !prefix.getOwner().isEmpty() && ((GenericChannelEvent) event).getChannel().getUsers().contains(event.getBot().getUserChannelDao().getUser(prefix.getOwner()))) {
                            continue;
                        }
                        CommandEvent cmdEvent = new CommandEvent(event.getBot(), messageEvent);
                        queue.add(cmdEvent);
                        break;
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

    public List<String> getCommandPrefixList() {
        List<String> prefixes = new LinkedList<>();
        for (CommandPrefix prefix : getCommandPrefixes()) {
            prefixes.add(prefix.getPrefix());
        }
        return prefixes;
    }

    protected List<CommandPrefix> getCommandPrefixes() {
        return commandChars;
    }

    protected Queue<Event> getQueue() {
        return queue;
    }

    protected PircBotY getBot() {
        return masterBot;
    }

    protected ExecutorService getExecutorService() {
        return execServ;
    }

    protected Map<Class<? extends Event>, Set<EventExecutorService>> getEventExecutors() {
        return eventExecutors;
    }

    protected Set<CommandExecutor> getCommandExecutors() {
        return commandExecutors;
    }
}

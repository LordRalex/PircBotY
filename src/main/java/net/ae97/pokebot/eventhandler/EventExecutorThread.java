/*
 * Copyright (C) 2014 Lord_Ralex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ae97.pokebot.eventhandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.api.events.CommandEvent;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.CommandExecutor;
import net.ae97.pokebot.api.Priority;
import net.ae97.pokebot.logger.PrefixLogger;

public class EventExecutorThread extends Thread {

    private final EventHandler handler;
    private final Logger logger = new PrefixLogger("EventExecutorThread", PokeBot.getLogger());

    protected EventExecutorThread(EventHandler handler) {
        this.handler = handler;
        setName("EventExecutor_" + getName());
    }

    @Override
    public void run() {
        boolean run = true;
        while (run) {
            Event next = handler.getQueue().poll();
            if (next == null) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        run = false;
                    }
                }
            } else if (next instanceof CommandEvent) {
                CommandEvent evt = (CommandEvent) next;
                /*
                 User user = evt.getUser();
                 Channel chan = evt.getChannel();
                 PermissionEvent permEvent = new PermissionEvent(masterBot, user);
                 try {
                 PokeBot.getPermManager().runPermissionEvent(permEvent);
                 } catch (Exception e) {
                 logger.log(Level.SEVERE, "Error on permission event", e);
                 continue;
                 }
                 */
                if (evt.getCommand().equalsIgnoreCase("reload")) {
                    User sender = evt.getUser();
                    if (sender != null) {
                        continue;
                    }
                    logger.log(Level.INFO, "Performing a reload, please hold");
                    if (sender != null) {
                        sender.send().notice("Reloading");
                    }
                    PokeBot.getExtensionManager().unload();
                    PokeBot.getEventHandler().unload();
                    PokeBot.getEventHandler().load();
                    PokeBot.getExtensionManager().load();
                    logger.log(Level.INFO, "Reloaded");
                    if (sender != null) {
                        sender.send().notice("Reloaded");
                    }
                } else if (evt.getCommand().equalsIgnoreCase("permreload")) {
                    User sender = evt.getUser();
                    if (sender != null) {
                        continue;
                    }
                    logger.log(Level.INFO, "Performing a permission reload, please hold");
                    if (sender != null) {
                        sender.send().notice("Reloading permissions");
                    }
                } else {
                    for (CommandExecutor exec : handler.getCommandExecutors()) {
                        if (Arrays.asList(exec.getAliases()).contains(evt.getCommand())) {
                            handler.getExecutorService().submit(new CommandRunnable(exec, evt));
                            break;
                        }
                    }
                }
            } else {
                Set<EventExecutorService> executors = handler.getEventExecutors().get(next.getClass());
                if (executors == null) {
                    continue;
                }
                for (Priority prio : Priority.values()) {
                    for (EventExecutorService exec : executors) {
                        if (exec.getPriority() == prio) {
                            try {
                                exec.getMethod().invoke(exec.getListener(), next);
                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                logger.log(Level.SEVERE, "Error on handling " + next.getClass().getName() + " in " + exec.getListener().getClass().getName(), e);
                            }
                        }
                    }
                }
            }
        }
        logger.log(Level.INFO, "Ending event listener");
    }

    protected void ping() {
        try {
            synchronized (this) {
                if (this.isAlive()) {
                    this.notifyAll();
                }
            }
        } catch (IllegalMonitorStateException e) {
            logger.log(Level.SEVERE, "Major issue on pinging event system", e);
        }
    }
}

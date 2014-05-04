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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.ae97.pircboty.api.events.CommandEvent;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.CommandExecutor;
import net.ae97.pokebot.logger.PrefixLogger;

public class CommandRunnable implements Runnable {

    private final CommandExecutor listener;
    private final CommandEvent event;
    private final Logger logger = new PrefixLogger("CommandExecutor", PokeBot.getLogger());

    public CommandRunnable(CommandExecutor list, CommandEvent evt) {
        listener = list;
        event = evt;
    }

    @Override
    public void run() {
        try {
            listener.runEvent(event);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error on executing command " + event.getCommand(), e);
        }
    }
}

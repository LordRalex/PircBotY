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
package net.ae97.pokebot.input;

import java.io.IOException;
import java.util.logging.Level;
import net.ae97.pircboty.Channel;
import net.ae97.pokebot.PokeBot;

public class ConsoleParser {

    private String currentChannel = null;

    protected ConsoleParser() {
    }

    protected void parse(String message) {
        String cmd = message.split(" ", 2)[0].toLowerCase();
        String[] args = message.split(" ").length >= 2 ? message.split(" ", 2)[1].split(" ") : new String[0];
        switch (cmd) {
            case "channel": {
                if (args.length != 1) {
                    PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: channel <channel>"));
                } else {
                    currentChannel = args[0];
                    Channel ch = PokeBot.getChannel(currentChannel);
                    PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Now talking in " + ch.getName()));
                }
            }
            break;
            case "tell":
            case "msg": {
                if (args.length > 2) {
                    PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: msg <user> <message>"));
                } else {
                    PokeBot.getBot().sendIRC().message(args[1], message.split(" ", 3)[2]);
                }
            }
            break;
            case "part": {
                if (args.length == 0) {
                    PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: part <channel>"));
                } else {
                    for (String arg : args) {
                        PokeBot.getBot().getOutputIRC().partChannel(arg);
                    }
                }
            }
            break;
            case "join": {
                if (args.length == 0) {
                    PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: join <channel>"));
                } else {
                    for (String arg : args) {
                        PokeBot.getBot().getOutputIRC().joinChannel(arg);
                    }
                }
            }
            break;
            case "nick": {
                if (args.length != 1) {
                    PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: nick <name>"));
                } else {
                    PokeBot.getBot().getOutputIRC().changeNick(args[0]);
                }
            }
            break;
            case "reload": {
                PokeBot.getEventHandler().unload();
                PokeBot.getExtensionManager().unload();
                try {
                    PokeBot.getPermManager().reload();
                } catch (IOException ex) {
                    PokeBot.getLogger().log(Level.SEVERE, "Error on reloading permissions", ex);
                }
                PokeBot.getEventHandler().load();
                PokeBot.getExtensionManager().load();
            }
            break;
            default: {
                if (currentChannel != null) {
                    PokeBot.getChannel(currentChannel).send().message(message);
                }
            }
            break;
        }
    }

}

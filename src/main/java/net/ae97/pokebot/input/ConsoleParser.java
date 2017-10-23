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

import java.util.logging.Level;
import net.ae97.pircboty.Channel;
import net.ae97.pokebot.PokeBot;

public class ConsoleParser {

    private String currentChannel = null;

    protected ConsoleParser() {
    }

    protected void parse(String message) {
        if (message.startsWith("$")) {
            String[] args = message.split(" ").length >= 2 ? message.split(" ", 2)[1].split(" ") : new String[0];
            switch (message.split(" ", 2)[0].toLowerCase().substring(1)) {
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
                    if (args.length < 2) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: msg <user> <message>"));
                    } else {
                        PokeBot.getBot().sendIRC().message(args[0], message.split(" ", 3)[2]);
                    }
                }
                case "me": {
                    if (args.length < 2) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: msg <user> <message>"));
                    } else {
                        PokeBot.getBot().sendIRC().action(args[0], message.split(" ", 3)[2]);
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
                    PokeBot.getEventHandler().load();
                    PokeBot.getExtensionManager().load();
                }
                break;
                case "voice": {
                    if (args.length != 2) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: voice <channel> <name>"));
                    } else {
                        PokeBot.getChannel(args[0]).send().voice(PokeBot.getUser(args[1]));
                    }
                }
                break;
                case "devoice": {
                    if (args.length != 2) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: devoice <channel> <name>"));
                    } else {
                        PokeBot.getChannel(args[0]).send().deVoice(PokeBot.getUser(args[1]));
                    }
                }
                break;
                case "op": {
                    if (args.length != 2) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: op <channel> <name>"));
                    } else {
                        PokeBot.getChannel(args[0]).send().op(PokeBot.getUser(args[1]));
                    }
                }
                break;
                case "deop": {
                    if (args.length != 2) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: deop <channel> <name>"));
                    } else {
                        PokeBot.getChannel(args[0]).send().deOp(PokeBot.getUser(args[1]));
                    }
                }
                break;
                case "kick": {
                    if (args.length < 1) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: kick <name>"));
                    } else {
                        if (currentChannel != null) {
                            String m = args[0];
                            if (args.length > 1) {
                                m = args[1];
                                for (int i = 2; i < args.length; i++) {
                                    m += args[i];
                                    m += " ";
                                }
                                m = m.trim();
                            }
                            PokeBot.getChannel(currentChannel).send().kick(PokeBot.getUser(args[0]), m);
                        }
                    }
                }
                break;
                case "ban": {
                    if (args.length < 1) {
                        PokeBot.getLogger().log(new ConsoleParserLogRecord(Level.INFO, "Usage: ban <mask>"));
                    } else if (currentChannel != null) {
                        PokeBot.getChannel(currentChannel).send().ban(args[0]);
                    }
                }
                break;
            }
        } else {
            if (currentChannel != null) {
                PokeBot.getChannel(currentChannel).send().message(message);
            }
        }
    }
}

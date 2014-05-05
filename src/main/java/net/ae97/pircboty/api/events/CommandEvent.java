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
package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelEvent;
import net.ae97.pircboty.generics.GenericChannelUserEvent;
import net.ae97.pircboty.generics.GenericMessageEvent;
import net.ae97.pircboty.generics.GenericUserEvent;
import net.ae97.pokebot.PokeBot;

/**
 *
 * @author Lord_Ralex
 */
public class CommandEvent extends Event implements GenericChannelUserEvent {

    private final User user;
    private final Channel channel;
    private final String command;
    private final String[] args;
    private final GenericMessageEvent parent;

    public CommandEvent(PircBotY bot, GenericMessageEvent event) {
        super(bot);
        this.parent = event;
        this.user = parent instanceof GenericUserEvent ? ((GenericUserEvent) parent).getUser() : null;
        this.channel = parent instanceof GenericChannelEvent ? ((GenericChannelEvent) parent).getChannel() : null;
        String cmd = parent.getMessage().split(" ")[0];
        for (String prefix : PokeBot.getEventHandler().getCommandPrefixList()) {
            if (cmd.startsWith(prefix)) {
                cmd = cmd.substring(prefix.length());
                break;
            }
        }
        this.command = cmd;
        if (parent.getMessage().split(" ", 2).length == 1) {
            args = new String[0];
        } else {
            args = parent.getMessage().split(" ", 2)[1].split(" ");
        }
    }

    @Override
    public void respond(String response) {
        if (channel != null) {
            channel.send().message(user.getNick() + ": " + response);
        } else {
            user.send().notice(response);
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    public GenericMessageEvent getParent() {
        return parent;
    }

}

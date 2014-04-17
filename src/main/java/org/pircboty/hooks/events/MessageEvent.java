/**
 * Copyright (C) 2010-2013
 *
 * This file is part of PircBotY.
 *
 * PircBotY is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotY is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotY. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircboty.hooks.events;

import org.pircboty.Channel;
import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericChannelUserEvent;
import org.pircboty.hooks.types.GenericMessageEvent;

/**
 * Used whenever a message is sent to a channel.
 *
 * @author
 */
public class MessageEvent<T extends PircBotY> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T> {

    private final Channel channel;
    private final User user;
    private final String message;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel to which the message was sent.
     * @param user The user who sent the message.
     * @param message The actual message sent to the channel.
     */
    public MessageEvent(T bot, Channel channel, User user, String message) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    /**
     * Respond with a channel message in <code>user: message</code> format to
     * the user that sent the message
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getChannel().send().message(getUser(), response);
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}

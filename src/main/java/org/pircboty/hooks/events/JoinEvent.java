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

/**
 * This event is dispatched whenever someone (possibly us) joins a channel which
 * we are on.
 *
 * @author
 */
public class JoinEvent<T extends PircBotY> extends Event<T> implements GenericChannelUserEvent<T> {

    private final Channel channel;
    private final User user;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel which somebody joined.
     * @param user The user who joined the channel.
     */
    public JoinEvent(T bot, Channel channel, User user) {
        super(bot);
        this.channel = channel;
        this.user = user;
    }

    /**
     * Respond with a channel message in <code>user: message</code> format to
     * the user that joined
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getChannel().send().message(getUser(), response);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return user;
    }
}

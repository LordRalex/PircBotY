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
import org.pircboty.hooks.types.GenericChannelModeEvent;

/**
 * Called when a channel key is removed.
 * <p>
 * This is a type of mode change and therefor is also dispatched in a
 * {@link org.PircBotY.hooks.events.ModeEvent}
 *
 * @author
 */
public class RemoveChannelKeyEvent<T extends PircBotY> extends Event<T> implements GenericChannelModeEvent<T> {

    private final Channel channel;
    private final User user;
    private final String key;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel in which the mode change took place.
     * @param user The user that performed the mode change.
     * @param key The key that was in use before the channel key was removed.
     */
    public RemoveChannelKeyEvent(T bot, Channel channel, User user, String key) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.key = key;
    }

    /**
     * Respond by send a message in the channel to the user that removed the
     * mode in <code>user: message</code> format
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

    public String getKey() {
        return key;
    }
}

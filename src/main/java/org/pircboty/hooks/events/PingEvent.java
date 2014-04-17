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
import org.pircboty.hooks.CoreHooks;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericCTCPEvent;

/**
 * This event is dispatched whenever we receive a PING request from another
 * user.
 * <p>
 * {@link CoreHooks} automatically responds correctly. Unless {@link CoreHooks}
 * is removed from the
 * {@link PircBotY#getListenerManager() bot's ListenerManager}, Listeners of
 * this event should <b>not</b> send a response as the user will get two
 * responses
 *
 * @author
 */
public class PingEvent<T extends PircBotY> extends Event<T> implements GenericCTCPEvent<T> {

    private final User user;
    private final Channel channel;
    private final String pingValue;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param user The user that sent the PING request.
     * @param channel The channel that received the ping request. A value of
     * <code>null</code> means the target was us.
     * @param pingValue The value that was supplied as an argument to the PING
     * command.
     */
    public PingEvent(T bot, User user, Channel channel, String pingValue) {
        super(bot);
        this.user = user;
        this.channel = channel;
        this.pingValue = pingValue;
    }

    /**
     * Respond with a CTCP response to the user
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getUser().send().ctcpResponse(response);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public String getPingValue() {
        return pingValue;
    }
}

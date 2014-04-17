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
import org.pircboty.UserChannelDao;
import org.pircboty.hooks.Event;

/**
 * This event is dispatched when we get disconnected. It is meant for the bot to
 * carry out actions upon disconnection. This may happen if the PircBotY quits
 * from the server, or if the connection is unexpectedly lost.
 * <p>
 * Disconnection from the IRC server is detected immediately if either we or the
 * server close the connection normally. If the connection to the server is
 * lost, but neither we nor the server have explicitly closed the connection,
 * then it may take a few minutes to detect (this is commonly referred to as a
 * "ping timeout").
 * <p>
 * If you wish to get your IRC bot to automatically rejoin a server after the
 * connection has been lost, then this is probably the ideal event listen for to
 * implement such functionality.
 *
 * @author
 */
public class DisconnectEvent<T extends PircBotY> extends Event<T> {

    private final UserChannelDao<T, ? extends User, ? extends Channel> daoSnapshot;
    private final Exception disconnectException;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     */
    public DisconnectEvent(T bot, UserChannelDao<T, ? extends User, ? extends Channel> daoSnapshot, Exception disconnectException) {
        super(bot);
        this.daoSnapshot = daoSnapshot;
        this.disconnectException = disconnectException;
    }

    /**
     * Does NOT respond to the server! This will throw an
     * {@link UnsupportedOperationException} since we can't respond to a server
     * we just disconnected from.
     *
     * @param response The response to send
     */
    @Override
    @Deprecated
    public void respond(String response) {
        throw new UnsupportedOperationException("Attepting to respond to a disconnected server");
    }
}

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
 * This event is dispatched whenever someone (possibly us) is kicked from any of
 * the channels that we are in.
 *
 * @author
 */
public class KickEvent<T extends PircBotY> extends Event<T> implements GenericChannelUserEvent<T> {

    private final Channel channel;
    private final User user;
    private final User recipient;
    private final String reason;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel from which the recipient was kicked.
     * @param user The user who performed the kick.
     * @param recipient The unfortunate recipient of the kick.
     * @param reason The reason given by the user who performed the kick.
     */
    public KickEvent(T bot, Channel channel, User user, User recipient, String reason) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.recipient = recipient;
        this.reason = reason;
    }

    /**
     * Respond with a channel message in <code>user: message</code> format to
     * the <i>user that preformed the kick</i>
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

    public User getRecipient() {
        return recipient;
    }

    public String getReason() {
        return reason;
    }
}

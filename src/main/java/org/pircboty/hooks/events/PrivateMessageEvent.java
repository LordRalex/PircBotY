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

import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericMessageEvent;

/**
 * This event is dispatched whenever a private message is sent to us.
 *
 * @author
 */
public class PrivateMessageEvent<T extends PircBotY> extends Event<T> implements GenericMessageEvent<T> {

    private final User user;
    private final String message;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param user The user who sent the private message.
     * @param message The actual message.
     */
    public PrivateMessageEvent(T bot, User user, String message) {
        super(bot);
        this.user = user;
        this.message = message;
    }

    /**
     * Respond with a private message to the user that sent the message
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getUser().send().message(response);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

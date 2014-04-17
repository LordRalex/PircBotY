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
import org.pircboty.hooks.types.GenericUserEvent;

/**
 * Called when the mode of a user is set.
 *
 * @author
 */
public class UserModeEvent<B extends PircBotY> extends Event<B> implements GenericUserEvent<B> {

    private final User user;
    private final User recipient;
    private final String mode;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param user The user that set the mode.
     * @param recipient The user that the mode operation applies to.
     * @param mode The mode that has been set.
     */
    public UserModeEvent(B bot, User user, User recipient, String mode) {
        super(bot);
        this.user = user;
        this.recipient = recipient;
        this.mode = mode;
    }

    /**
     * Respond with a private message to the source user
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

    public User getRecipient() {
        return recipient;
    }

    public String getMode() {
        return mode;
    }
}

/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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

import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.pircboty.PircBotY;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericUserEvent;
import org.pircboty.snapshot.UserChannelDaoSnapshot;
import org.pircboty.snapshot.UserSnapshot;

/**
 * This event is dispatched whenever someone (possibly us) quits from the
 * server. We will only observe this if the user was in one of the channels to
 * which we are connected.
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuitEvent<T extends PircBotY> extends Event<T> implements GenericUserEvent<T> {

    protected final UserChannelDaoSnapshot daoSnapshot;
    protected final UserSnapshot user;
    protected final String reason;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param user The user that quit from the server in snapshot form
     * @param reason The reason given for quitting the server.
     */
    public QuitEvent(T bot, @NonNull UserChannelDaoSnapshot daoSnapshot, @NonNull UserSnapshot user, @NonNull String reason) {
        super(bot);
        this.daoSnapshot = daoSnapshot;
        this.user = user;
        this.reason = reason;
    }

    /**
     * Does NOT respond! This will throw an
     * {@link UnsupportedOperationException} since we can't respond to a user
     * that just quit
     *
     * @param response The response to send
     */
    @Override
    @Deprecated
    public void respond(@Nullable String response) {
        throw new UnsupportedOperationException("Attempting to respond to a user that quit");
    }
}

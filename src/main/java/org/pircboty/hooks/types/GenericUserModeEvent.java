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
package org.pircboty.hooks.types;

import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.hooks.events.OpEvent;

/**
 * Any user status change in a channel. Eg {@link OpEvent}
 * <p/>
 * @author
 */
public interface GenericUserModeEvent<T extends PircBotY> extends GenericChannelUserEvent<T> {

    /**
     * The recipient of the mode change
     *
     * @return The recipient user
     */
    public User getRecipient();
}

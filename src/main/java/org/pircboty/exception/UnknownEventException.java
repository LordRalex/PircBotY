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
package org.pircboty.exception;

import org.apache.commons.lang3.Validate;
import org.pircboty.PircBotY;
import org.pircboty.hooks.Event;

/**
 * An unknown event error.
 *
 * @author
 */
public class UnknownEventException extends RuntimeException {

    private static final long serialVersionUID = 40292L;

    public UnknownEventException(Event<? extends PircBotY> event, Throwable cause) {
        super("Unknown Event " + (event == null ? null : event.getClass().toString()), cause);
        Validate.notNull(event, "Event cannot be null");
    }

    public UnknownEventException(Event<? extends PircBotY> event) {
        this(event, null);
    }
}

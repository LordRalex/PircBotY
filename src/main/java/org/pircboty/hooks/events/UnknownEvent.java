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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircboty.PircBotY;
import org.pircboty.hooks.Event;

/**
 * This event is dispatched whenever we receive a line from the server that
 * PircBotY has not been programmed to recognize.
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnknownEvent<T extends PircBotY> extends Event<T> {

    @Getter(onMethod = @_({
        @Nonnull}))
    protected final String line;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param line The raw line that was received from the server.
     */
    public UnknownEvent(T bot, @NonNull String line) {
        super(bot);
        this.line = line;
    }

    /**
     * Responds by sending a <b>raw line</b> to the server.
     *
     * @param response The response to send
     */
    @Override
    public void respond(@Nullable String response) {
        getBot().sendRaw().rawLine(response);
    }
}

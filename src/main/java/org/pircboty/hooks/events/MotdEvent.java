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

/**
 * This event is dispatched when the Motd is finished being sent. Motd lines are
 * separated by <code>\n</code>
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MotdEvent<T extends PircBotY> extends Event<T> {

    protected final String motd;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param motd The full motd separated by newlines (<code>\n</code>)
     */
    public MotdEvent(T bot, @NonNull String motd) {
        super(bot);
        this.motd = motd;
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

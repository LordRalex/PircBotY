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
package org.pircboty.hooks;

import org.pircboty.PircBotY;

/**
 * Listener that provides an easy way to make temporary listeners that aren't
 * needed after one use
 * <p>
 * Your listener will only get called if the bot that generated the event
 * matches the given one. Then when you are finished, call {@link #done()} and
 * the listener will be removed.
 * <p>
 * Note: The reason the {@link #done()} method is used instead of automatically
 * removing is that you may need to check for something before executing.
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
public class TemporaryListener extends ListenerAdapter<PircBotY> {

    protected final PircBotY bot;

    public TemporaryListener(PircBotY bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(Event<PircBotY> event) throws Exception {
        if (event.getBot() == bot) {
            super.onEvent(event);
        }
    }

    public void done() {
        bot.getConfiguration().getListenerManager().removeListener(this);
    }
}

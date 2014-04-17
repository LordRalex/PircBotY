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
package org.pircboty.hooks;

import com.google.common.collect.ComparisonChain;
import org.pircboty.PircBotY;
import org.pircboty.hooks.managers.ListenerManager;
import org.pircboty.hooks.types.GenericEvent;

/**
 * An event representing what was received from the IRC server.
 *
 * @author
 */
public abstract class Event<T extends PircBotY> implements GenericEvent<T> {

    private final long timestamp;
    private final T bot;
    private final long id;

    public Event(T bot) {
        this(bot, bot.getConfiguration().getListenerManager());
    }

    public Event(ListenerManager<? extends PircBotY> listenerManager) {
        this(null, listenerManager);
    }

    public Event(T bot, ListenerManager<? extends PircBotY> listenerManager) {
        this.timestamp = System.currentTimeMillis();
        this.bot = bot;
        this.id = listenerManager.incrementCurrentId();
    }

    /**
     * Returns the {@link PircBotY} instance that this event originally came
     * from.
     *
     * @return A {@link PircBotY} instance
     */
    @Override
    public T getBot() {
        return bot;
    }

    /**
     * Returns the timestamp of when the event was created.
     *
     * @return A timestamp as a long
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the id of this event. This id is guaranteed to be unique in the
     * context of other events dispatched from the same listener manager.
     *
     * @return The id of this event
     */
    public long getId() {
        return id;
    }

    /**
     * Compare events by {@link #getTimestamp()} and then {@link #getId()} to
     * order by when they are received. This is useful for sorting lists of
     * Channel objects.
     *
     * @param other Other Event to compare to
     * @return the result of the comparison
     */
    @Override
    public int compareTo(Event<T> other) {
        ComparisonChain comparison = ComparisonChain.start()
                .compare(getTimestamp(), other.getTimestamp())
                .compare(getId(), other.getId());
        if (bot != null && other.getBot() != null) {
            comparison.compare(bot.getBotId(), other.getBot().getBotId());
        }
        return comparison.result();
    }
}

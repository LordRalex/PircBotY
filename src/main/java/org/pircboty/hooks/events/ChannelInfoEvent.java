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

import com.google.common.collect.ImmutableList;
import org.pircboty.ChannelListEntry;
import org.pircboty.PircBotY;
import org.pircboty.hooks.Event;

/**
 * After calling the listChannels() method in PircBotY, the server will start to
 * send us information about each channel on the server. You may listen for this
 * event in order to receive the information about each channel as soon as it is
 * received.
 * <p>
 * Note that certain channels, such as those marked as hidden, may not appear in
 * channel listings.
 *
 * @author
 * @see PircBotY#listChannels()
 * @see PircBotY#listChannels(java.lang.String)
 */
public class ChannelInfoEvent<T extends PircBotY> extends Event<T> {

    private final ImmutableList<ChannelListEntry> list;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param list A list of ChannelList Entries
     */
    public ChannelInfoEvent(T bot, ImmutableList<ChannelListEntry> list) {
        super(bot);
        this.list = list;
    }

    /**
     * Respond by sending a <b>raw line</b> to the server.
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public ImmutableList<ChannelListEntry> getList() {
        return list;
    }
}

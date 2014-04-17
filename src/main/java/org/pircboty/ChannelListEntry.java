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
package org.pircboty;

/**
 * A channel entry returned from /LIST command.
 *
 * @author
 */
public class ChannelListEntry {

    /**
     * Channel name
     */
    private final String name;
    /**
     * Number of users currently in the channel
     */
    private final int users;
    /**
     * The current channel topic
     */
    private final String topic;

    public ChannelListEntry(String name, int users, String topic) {
        this.name = name;
        this.users = users;
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public int getUsers() {
        return users;
    }

    public String getTopic() {
        return topic;
    }
}

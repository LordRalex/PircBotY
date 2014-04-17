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
package org.pircboty.snapshot;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSortedSet;
import java.util.EnumMap;
import java.util.Locale;
import org.pircboty.PircBotY;
import org.pircboty.UserChannelDao;
import org.pircboty.UserChannelMap;
import org.pircboty.UserLevel;

/**
 *
 * @author Leon
 */
public class UserChannelDaoSnapshot extends UserChannelDao<UserSnapshot, ChannelSnapshot> {

    public UserChannelDaoSnapshot(PircBotY bot, Locale locale, UserChannelMapSnapshot mainMap, EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMap, ImmutableBiMap<String, UserSnapshot> userNickMap, ImmutableBiMap<String, ChannelSnapshot> channelNameMap, ImmutableSortedSet<UserSnapshot> privateUsers) {
        super(bot, null, locale, mainMap, levelsMap, userNickMap, channelNameMap, privateUsers);
    }

    @Override
    public UserSnapshot getUser(String nick) {
        UserSnapshot user = userNickMap.get(nick.toLowerCase());
        if (user == null) {
            throw new RuntimeException("User " + nick + " does not exist");
        }
        return user;
    }

    @Override
    public ChannelSnapshot getChannel(String name) {
        ChannelSnapshot channel = channelNameMap.get(name.toLowerCase());
        if (channel == null) {
            throw new RuntimeException("Channel " + channel + " does not exist");
        }
        return channel;
    }

    @Override
    protected void removeUserFromChannel(UserSnapshot user, ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    protected void removeUser(UserSnapshot user) {
        SnapshotUtils.fail();
    }

    @Override
    protected void renameUser(UserSnapshot user, String newNick) {
        SnapshotUtils.fail();
    }

    @Override
    protected void removeChannel(ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    protected void addUserToChannel(UserSnapshot user, ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    protected void addUserToPrivate(UserSnapshot user) {
        SnapshotUtils.fail();
    }

    @Override
    protected void addUserToLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    protected void removeUserFromLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    public void close() {
        SnapshotUtils.fail();
    }
}

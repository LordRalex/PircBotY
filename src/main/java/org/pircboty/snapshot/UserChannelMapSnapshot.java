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

import com.google.common.collect.Multimap;
import org.pircboty.UserChannelMap;

/**
 *
 * @author Leon
 */
public class UserChannelMapSnapshot extends UserChannelMap<UserSnapshot, ChannelSnapshot> {

    public UserChannelMapSnapshot(Multimap<UserSnapshot, ChannelSnapshot> userToChannelSnapshot, Multimap<ChannelSnapshot, UserSnapshot> channelToUserSnapshot) {
        super(userToChannelSnapshot, channelToUserSnapshot);
    }

    @Override
    public void removeUserFromChannel(UserSnapshot user, ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    public void removeUser(UserSnapshot user) {
        SnapshotUtils.fail();
    }

    @Override
    public void removeChannel(ChannelSnapshot channel) {
        SnapshotUtils.fail();
    }

    @Override
    public void clear() {
        SnapshotUtils.fail();
    }
}

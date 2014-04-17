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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import java.util.Map;
import org.pircboty.snapshot.ChannelSnapshot;
import org.pircboty.snapshot.UserChannelMapSnapshot;
import org.pircboty.snapshot.UserSnapshot;

/**
 * A many to many map of users to channels.
 */
public class UserChannelMap<U extends User, C extends Channel> {

    protected final Multimap<U, C> userToChannelMap;
    protected final Multimap<C, U> channelToUserMap;

    /**
     * Create with HashMultimaps.
     */
    public UserChannelMap() {
        channelToUserMap = HashMultimap.create();
        userToChannelMap = HashMultimap.create();
    }

    public UserChannelMap(Multimap<U, C> userToChannelMap, Multimap<C, U> channelToUserMap) {
        this.userToChannelMap = userToChannelMap;
        this.channelToUserMap = channelToUserMap;
    }

    public void addUserToChannel(U user, C channel) {
        userToChannelMap.put(user, channel);
        channelToUserMap.put(channel, user);
    }

    public void removeUserFromChannel(U user, C channel) {
        userToChannelMap.remove(user, channel);
        channelToUserMap.remove(channel, user);
    }

    public void removeUser(U user) {
        //Remove the user from each channel
        for (Channel curChannel : userToChannelMap.removeAll(user)) {
            channelToUserMap.remove(curChannel, user);
        }
    }

    public void removeChannel(C channel) {
        //Remove the channel from each user
        for (User curUser : channelToUserMap.removeAll(channel)) //This will automatically remove the user if they have no more channels
        {
            userToChannelMap.remove(curUser, channel);
        }
    }

    public ImmutableSortedSet<U> getUsers(C channel) {
        return ImmutableSortedSet.copyOf(channelToUserMap.get(channel));
    }

    public ImmutableSortedSet<C> getChannels(U user) {
        return ImmutableSortedSet.copyOf(userToChannelMap.get(user));
    }

    public boolean containsEntry(U user, C channel) {
        boolean channelToUserContains = channelToUserMap.containsEntry(channel, user);
        boolean userToChannelContains = userToChannelMap.containsEntry(user, channel);
        if (channelToUserContains != userToChannelContains) {
            throw new RuntimeException("Map inconsistent! User: " + user + " | Channel: " + channel + " | channelToUserMap: " + channelToUserContains + " | userToChannelMap: " + userToChannelContains);
        }
        return channelToUserContains;
    }

    public boolean containsUser(User user) {
        boolean channelToUserContains = channelToUserMap.containsValue(user);
        boolean userToChannelContains = userToChannelMap.containsKey(user);
        if (channelToUserContains != userToChannelContains) {
            throw new RuntimeException("Map inconsistent! User: " + user + " | channelToUserMap: " + channelToUserContains + " | userToChannelMap: " + userToChannelContains);
        }
        return channelToUserContains;
    }

    public void clear() {
        userToChannelMap.clear();
        channelToUserMap.clear();
    }

    public UserChannelMapSnapshot createSnapshot(Map<U, UserSnapshot> userSnapshots, Map<C, ChannelSnapshot> channelSnapshots) {
        //Create new multimaps replacing each user and channel with their respective snapshots
        ImmutableMultimap.Builder<UserSnapshot, ChannelSnapshot> userToChannelSnapshotBuilder = ImmutableMultimap.builder();
        for (Map.Entry<U, C> curEntry : userToChannelMap.entries()) {
            userToChannelSnapshotBuilder.put(userSnapshots.get(curEntry.getKey()), channelSnapshots.get(curEntry.getValue()));
        }
        ImmutableMultimap.Builder<ChannelSnapshot, UserSnapshot> channelToUserSnapshotBuilder = ImmutableMultimap.builder();
        for (Map.Entry<C, U> curEntry : channelToUserMap.entries()) {
            channelToUserSnapshotBuilder.put(channelSnapshots.get(curEntry.getKey()), userSnapshots.get(curEntry.getValue()));
        }
        //Return a snapshot of the map
        return new UserChannelMapSnapshot(userToChannelSnapshotBuilder.build(), channelToUserSnapshotBuilder.build());
    }
}

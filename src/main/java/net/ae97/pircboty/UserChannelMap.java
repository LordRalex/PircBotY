package net.ae97.pircboty;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserChannelMapSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;

public class UserChannelMap<U extends User, C extends Channel> {

    protected final Multimap<U, C> userToChannelMap;
    protected final Multimap<C, U> channelToUserMap;

    public UserChannelMap() {
        channelToUserMap = HashMultimap.create();
        userToChannelMap = HashMultimap.create();
    }

    public UserChannelMap(Multimap<U, C> userToChannelMap, Multimap<C, U> channelToUserMap) {
        this.userToChannelMap = userToChannelMap;
        this.channelToUserMap = channelToUserMap;
    }

    protected void addUserToChannel(U user, C channel) {
        userToChannelMap.put(user, channel);
        channelToUserMap.put(channel, user);
    }

    protected void removeUserFromChannel(U user, C channel) {
        userToChannelMap.remove(user, channel);
        channelToUserMap.remove(channel, user);
    }

    protected void removeUser(U user) {
        for (Channel curChannel : userToChannelMap.removeAll(user)) {
            channelToUserMap.remove(curChannel, user);
        }
    }

    protected void removeChannel(C channel) {
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

    protected void clear() {
        userToChannelMap.clear();
        channelToUserMap.clear();
    }

    public UserChannelMapSnapshot createSnapshot(Map<User, UserSnapshot> userSnapshots, Map<Channel, ChannelSnapshot> channelSnapshots) {
        ImmutableMultimap.Builder<UserSnapshot, ChannelSnapshot> userToChannelSnapshotBuilder = ImmutableMultimap.builder();
        for (Map.Entry<U, C> curEntry : userToChannelMap.entries()) {
            userToChannelSnapshotBuilder.put(userSnapshots.get(curEntry.getKey()), channelSnapshots.get(curEntry.getValue()));
        }
        ImmutableMultimap.Builder<ChannelSnapshot, UserSnapshot> channelToUserSnapshotBuilder = ImmutableMultimap.builder();
        for (Map.Entry<C, U> curEntry : channelToUserMap.entries()) {
            channelToUserSnapshotBuilder.put(channelSnapshots.get(curEntry.getKey()), userSnapshots.get(curEntry.getValue()));
        }
        return new UserChannelMapSnapshot(userToChannelSnapshotBuilder.build(), channelToUserSnapshotBuilder.build());
    }
}

package net.ae97.pircboty;

import java.util.HashMap;
import java.util.Map;
import net.ae97.generics.MultiMap;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserChannelMapSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;
import net.ae97.generics.maps.MultiSetHashMap;
import net.ae97.generics.sets.ImmutableHashSet;
import net.ae97.generics.maps.ImmutableMultiSetMap;
import net.ae97.generics.sets.ImmutableSet;

public class UserChannelMap<U extends User, C extends Channel> {

    protected final MultiMap<U, C> userToChannelMap;
    protected final MultiMap<C, U> channelToUserMap;

    public UserChannelMap() {
        channelToUserMap = new MultiSetHashMap<>();
        userToChannelMap = new MultiSetHashMap<>();
    }

    public UserChannelMap(MultiMap<U, C> userToChannelMap, MultiMap<C, U> channelToUserMap) {
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
        for (User curUser : channelToUserMap.removeAll(channel)) {
            userToChannelMap.remove(curUser, channel);
        }
    }

    public ImmutableSet<U> getUsers(C channel) {
        return new ImmutableHashSet<>(channelToUserMap.get(channel));
    }

    public ImmutableHashSet<C> getChannels(U user) {
        return new ImmutableHashSet<>(userToChannelMap.get(user));
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
        HashMap<UserSnapshot, ChannelSnapshot> userToChannelSnapshotBuilder = new HashMap<>();
        for (Map.Entry<U, C> curEntry : userToChannelMap.entries()) {
            userToChannelSnapshotBuilder.put(userSnapshots.get(curEntry.getKey()), channelSnapshots.get(curEntry.getValue()));
        }
        HashMap<ChannelSnapshot, UserSnapshot> channelToUserSnapshotBuilder = new HashMap<>();
        for (Map.Entry<C, U> curEntry : channelToUserMap.entries()) {
            channelToUserSnapshotBuilder.put(channelSnapshots.get(curEntry.getKey()), userSnapshots.get(curEntry.getValue()));
        }
        return new UserChannelMapSnapshot(new ImmutableMultiSetMap<>(userToChannelSnapshotBuilder), new ImmutableMultiSetMap<>(channelToUserSnapshotBuilder));
    }
}

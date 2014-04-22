package net.ae97.pircboty.snapshot;

import net.ae97.pircboty.UserChannelMap;
import net.ae97.generics.MultiMap;

public class UserChannelMapSnapshot extends UserChannelMap<UserSnapshot, ChannelSnapshot> {

    public UserChannelMapSnapshot(MultiMap<UserSnapshot, ChannelSnapshot> userToChannelSnapshot, MultiMap<ChannelSnapshot, UserSnapshot> channelToUserSnapshot) {
        super(userToChannelSnapshot, channelToUserSnapshot);
    }

    @Override
    public void removeUserFromChannel(UserSnapshot user, ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to remove user from a channel in a snapshot");
    }

    @Override
    public void removeUser(UserSnapshot user) {
        throw new UnsupportedOperationException("Attempting to remove user from snapshot");
    }

    @Override
    public void removeChannel(ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to remove channel from snapshot");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Attempting to clear snapshot");
    }
}

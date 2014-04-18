package net.ae97.pircboty.snapshot;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSortedSet;
import java.util.EnumMap;
import java.util.Locale;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.UserChannelDao;
import net.ae97.pircboty.UserChannelMap;
import net.ae97.pircboty.UserLevel;

public class UserChannelDaoSnapshot<P extends PircBotY> extends UserChannelDao<P, UserSnapshot, ChannelSnapshot> {

    public UserChannelDaoSnapshot(P bot, Locale locale, UserChannelMapSnapshot mainMap, EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMap, ImmutableBiMap<String, UserSnapshot> userNickMap, ImmutableBiMap<String, ChannelSnapshot> channelNameMap, ImmutableSortedSet<UserSnapshot> privateUsers) {
        super(bot, null, locale, mainMap, levelsMap, userNickMap, channelNameMap, privateUsers);
    }

    @Override
    public UserSnapshot getUser(String nick) {
        UserSnapshot user = getUserNickMap().get(nick.toLowerCase());
        if (user == null) {
            throw new RuntimeException("User " + nick + " does not exist");
        }
        return user;
    }

    @Override
    public ChannelSnapshot getChannel(String name) {
        ChannelSnapshot channel = getChannelNameMap().get(name.toLowerCase());
        if (channel == null) {
            throw new RuntimeException("Channel " + channel + " does not exist");
        }
        return channel;
    }

    @Override
    protected void removeUserFromChannel(UserSnapshot user, ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void removeUser(UserSnapshot user) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void renameUser(UserSnapshot user, String newNick) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void removeChannel(ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void addUserToChannel(UserSnapshot user, ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void addUserToPrivate(UserSnapshot user) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void addUserToLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    protected void removeUserFromLevel(UserLevel level, UserSnapshot user, ChannelSnapshot channel) {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Attempting to modify a snapshot object");
    }
}

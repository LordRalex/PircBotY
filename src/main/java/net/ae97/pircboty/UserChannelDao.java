package net.ae97.pircboty;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import java.io.Closeable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserChannelDaoSnapshot;
import net.ae97.pircboty.snapshot.UserChannelMapSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;
import org.apache.commons.lang3.Validate;

public class UserChannelDao<P extends PircBotY, U extends User, C extends Channel> implements Closeable {

    private final P bot;
    private final BotFactory<P, U, C> botFactory;
    private final Locale locale;
    private final Object accessLock = new Object();
    private final UserChannelMap<U, C> mainMap;
    private final EnumMap<UserLevel, UserChannelMap<U, C>> levelsMap;
    private final BiMap<String, U> userNickMap;
    private final BiMap<String, C> channelNameMap;
    private final Set<U> privateUsers;

    public UserChannelDao(P bot, BotFactory<P, U, C> botFactory) {
        this.bot = bot;
        this.botFactory = botFactory;
        this.locale = bot.getConfiguration().getLocale();
        this.mainMap = new UserChannelMap<U, C>();
        this.userNickMap = HashBiMap.create();
        this.channelNameMap = HashBiMap.create();
        this.privateUsers = new HashSet<U>();
        this.levelsMap = Maps.newEnumMap(UserLevel.class);
        for (UserLevel level : UserLevel.values()) {
            levelsMap.put(level, new UserChannelMap<U, C>());
        }
    }

    public UserChannelDao(P bot, BotFactory<P, U, C> botFactory, Locale locale, UserChannelMap<U, C> mainMap, EnumMap<UserLevel, UserChannelMap<U, C>> levelsMap, BiMap<String, U> userNickMap, BiMap<String, C> channelNameMap, Set<U> privateUsers) {
        this.bot = bot;
        this.botFactory = botFactory;
        this.locale = locale;
        this.mainMap = mainMap;
        this.levelsMap = levelsMap;
        this.userNickMap = userNickMap;
        this.channelNameMap = channelNameMap;
        this.privateUsers = privateUsers;
    }

    public U getUser(String nick) {
        Validate.notBlank(nick, "Cannot get a blank user");
        U user = userNickMap.get(nick.toLowerCase(locale));
        if (user != null) {
            return user;
        }
        if (botFactory == null) {
            throw new UnsupportedOperationException("Dao cannot create new user");
        }
        user = botFactory.createUser(bot, nick);
        userNickMap.put(nick.toLowerCase(locale), user);
        return user;
    }

    public boolean userExists(String nick) {
        return userNickMap.containsKey(nick.toLowerCase(locale));
    }

    public ImmutableSortedSet<U> getAllUsers() {
        return ImmutableSortedSet.copyOf(userNickMap.values());
    }

    protected void addUserToChannel(U user, C channel) {
        mainMap.addUserToChannel(user, channel);
    }

    protected void addUserToPrivate(U user) {
        privateUsers.add(user);
    }

    protected void addUserToLevel(UserLevel level, U user, C channel) {
        levelsMap.get(level).addUserToChannel(user, channel);
    }

    protected void removeUserFromLevel(UserLevel level, U user, C channel) {
        levelsMap.get(level).removeUserFromChannel(user, channel);
    }

    public ImmutableSortedSet<U> getNormalUsers(C channel) {
        Set<U> remainingUsers = new HashSet<U>(mainMap.getUsers(channel));
        for (UserChannelMap<U, C> curLevelMap : levelsMap.values()) {
            remainingUsers.removeAll(curLevelMap.getUsers(channel));
        }
        return ImmutableSortedSet.copyOf(remainingUsers);
    }

    public ImmutableSortedSet<U> getUsers(C channel, UserLevel level) {
        return levelsMap.get(level).getUsers(channel);
    }

    public ImmutableSortedSet<UserLevel> getLevels(C channel, U user) {
        ImmutableSortedSet.Builder<UserLevel> builder = ImmutableSortedSet.naturalOrder();
        for (Map.Entry<UserLevel, UserChannelMap<U, C>> curEntry : levelsMap.entrySet()) {
            if (curEntry.getValue().containsEntry(user, channel)) {
                builder.add(curEntry.getKey());
            }
        }
        return builder.build();
    }

    public ImmutableSortedSet<C> getNormalUserChannels(U user) {
        Set<C> remainingChannels = new HashSet<C>(mainMap.getChannels(user));
        for (UserChannelMap<U, C> curLevelMap : levelsMap.values()) {
            remainingChannels.removeAll(curLevelMap.getChannels(user));
        }
        return ImmutableSortedSet.copyOf(remainingChannels);
    }

    public ImmutableSortedSet<C> getChannels(U user, UserLevel level) {
        return levelsMap.get(level).getChannels(user);
    }

    protected void removeUserFromChannel(U user, C channel) {
        mainMap.removeUserFromChannel(user, channel);
        for (UserChannelMap<U, C> curLevelMap : levelsMap.values()) {
            curLevelMap.removeUserFromChannel(user, channel);
        }
        if (!privateUsers.contains(user) && !mainMap.containsUser(user)) //Completely remove user
        {
            userNickMap.inverse().remove(user);
        }
    }

    protected void removeUser(U user) {
        mainMap.removeUser(user);
        for (UserChannelMap<U, C> curLevelMap : levelsMap.values()) {
            curLevelMap.removeUser(user);
        }
        userNickMap.inverse().remove(user);
        privateUsers.remove(user);
    }

    protected boolean levelContainsUser(UserLevel level, C channel, U user) {
        return levelsMap.get(level).containsEntry(user, channel);
    }

    protected void renameUser(U user, String newNick) {
        user.setNick(newNick);
        userNickMap.inverse().remove(user);
        userNickMap.put(newNick.toLowerCase(locale), user);
    }

    public C getChannel(String name) {
        Validate.notBlank(name, "Cannot get a blank channel");
        C chan = channelNameMap.get(name.toLowerCase(locale));
        if (chan != null) {
            return chan;
        }
        if (botFactory == null) {
            throw new UnsupportedOperationException("Dao cannot create new channel");
        }
        chan = botFactory.createChannel(bot, name);
        channelNameMap.put(name.toLowerCase(locale), chan);
        return chan;
    }

    public boolean channelExists(String name) {
        return channelNameMap.containsKey(name.toLowerCase(locale));
    }

    public ImmutableSortedSet<U> getUsers(C channel) {
        return mainMap.getUsers(channel);
    }

    public ImmutableSortedSet<C> getAllChannels() {
        return ImmutableSortedSet.copyOf(channelNameMap.values());
    }

    public ImmutableSortedSet<C> getChannels(U user) {
        return mainMap.getChannels(user);
    }

    protected void removeChannel(C channel) {
        mainMap.removeChannel(channel);
        for (UserChannelMap<U, C> curLevelMap : levelsMap.values()) {
            curLevelMap.removeChannel(channel);
        }
        channelNameMap.inverse().remove(channel);
    }

    @Override
    public void close() {
        mainMap.clear();
        for (UserChannelMap<U, C> curLevelMap : levelsMap.values()) {
            curLevelMap.clear();
        }
        channelNameMap.clear();
        privateUsers.clear();
        userNickMap.clear();
    }

    public UserChannelDaoSnapshot<P> createSnapshot() {
        ImmutableMap.Builder<U, UserSnapshot> userSnapshotBuilder = ImmutableMap.builder();
        for (U curUser : userNickMap.values()) {
            userSnapshotBuilder.put(curUser, curUser.createSnapshot());
        }
        ImmutableMap<U, UserSnapshot> userSnapshotMap = userSnapshotBuilder.build();
        ImmutableMap.Builder<C, ChannelSnapshot> channelSnapshotBuilder = ImmutableMap.builder();
        for (C curChannel : channelNameMap.values()) {
            channelSnapshotBuilder.put(curChannel, curChannel.createSnapshot());
        }
        ImmutableMap<C, ChannelSnapshot> channelSnapshotMap = channelSnapshotBuilder.build();
        UserChannelMapSnapshot mainMapSnapshot = mainMap.createSnapshot(userSnapshotMap, channelSnapshotMap);
        EnumMap<UserLevel, UserChannelMap<UserSnapshot, ChannelSnapshot>> levelsMapSnapshot = Maps.newEnumMap(UserLevel.class);
        for (Map.Entry<UserLevel, UserChannelMap<U, C>> curLevel : levelsMap.entrySet()) {
            levelsMapSnapshot.put(curLevel.getKey(), curLevel.getValue().createSnapshot(userSnapshotMap, channelSnapshotMap));
        }
        ImmutableBiMap.Builder<String, UserSnapshot> userNickMapSnapshotBuilder = ImmutableBiMap.builder();
        for (Map.Entry<String, U> curNick : userNickMap.entrySet()) {
            userNickMapSnapshotBuilder.put(curNick.getKey(), curNick.getValue().createSnapshot());
        }
        ImmutableBiMap.Builder<String, ChannelSnapshot> channelNameMapSnapshotBuilder = ImmutableBiMap.builder();
        for (Map.Entry<String, C> curName : channelNameMap.entrySet()) {
            channelNameMapSnapshotBuilder.put(curName.getKey(), curName.getValue().createSnapshot());
        }
        ImmutableSortedSet.Builder<UserSnapshot> privateUserSnapshotBuilder = ImmutableSortedSet.naturalOrder();
        for (User curUser : privateUsers) {
            privateUserSnapshotBuilder.add(curUser.createSnapshot());
        }
        UserChannelDaoSnapshot<P> daoSnapshot = new UserChannelDaoSnapshot<P>(
                bot,
                locale,
                mainMapSnapshot,
                levelsMapSnapshot,
                userNickMapSnapshotBuilder.build(),
                channelNameMapSnapshotBuilder.build(),
                privateUserSnapshotBuilder.build());
        for (UserSnapshot curUserSnapshot : userSnapshotMap.values()) {
            curUserSnapshot.setDao(daoSnapshot);
        }
        for (ChannelSnapshot curChannelSnapshot : channelSnapshotMap.values()) {
            curChannelSnapshot.setDao(daoSnapshot);
        }
        return daoSnapshot;
    }

    protected BiMap<String, U> getUserNickMap() {
        return userNickMap;
    }

    protected BiMap<String, C> getChannelNameMap() {
        return channelNameMap;
    }
}

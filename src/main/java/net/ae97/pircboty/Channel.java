package net.ae97.pircboty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import net.ae97.pircboty.output.OutputChannel;
import net.ae97.pircboty.snapshot.ChannelSnapshot;

public class Channel implements Comparable<Channel> {

    private final String name;
    private final UUID channelId = UUID.randomUUID();
    private final UserChannelDao dao;
    private final PircBotY bot;
    private final AtomicSafeInitializer<OutputChannel> output = new AtomicSafeInitializer<OutputChannel>() {
        @Override
        protected OutputChannel initialize() {
            return bot.getConfiguration().getBotFactory().createOutputChannel(bot, Channel.this);
        }
    };
    private String mode = "";
    private String topic = "";
    private long topicTimestamp;
    private long createTimestamp;
    private String topicSetter = "";
    private boolean moderated = false;
    private boolean noExternalMessages = false;
    private boolean inviteOnly = false;
    private boolean secret = false;
    private boolean channelPrivate = false;
    private boolean topicProtection = false;
    private int channelLimit = -1;
    private String channelKey = null;
    private boolean modeStale = false;
    private CountDownLatch modeLatch = null;

    protected Channel(PircBotY bot, UserChannelDao dao, String name) {
        this.bot = bot;
        this.dao = dao;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UserChannelDao getDao() {
        return dao;
    }

    public PircBotY getBot() {
        return bot;
    }

    public AtomicSafeInitializer<OutputChannel> getOutput() {
        return output;
    }

    public String getTopic() {
        return topic;
    }

    public long getTopicTimestamp() {
        return topicTimestamp;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public boolean isModerated() {
        return moderated;
    }

    public boolean isNoExternalMessages() {
        return noExternalMessages;
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    public boolean isSecret() {
        return secret;
    }

    public boolean isChannelPrivate() {
        return channelPrivate;
    }

    public boolean isTopicProtection() {
        return topicProtection;
    }

    public int getChannelLimit() {
        return channelLimit;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public boolean isModeStale() {
        return modeStale;
    }

    public CountDownLatch getModeLatch() {
        return modeLatch;
    }

    protected void setTopic(String topic) {
        this.topic = topic;
    }

    protected void setTopicTimestamp(long topicTimestamp) {
        this.topicTimestamp = topicTimestamp;
    }

    protected void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    protected void setTopicSetter(String topicSetter) {
        this.topicSetter = topicSetter;
    }

    protected void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    protected void setNoExternalMessages(boolean noExternalMessages) {
        this.noExternalMessages = noExternalMessages;
    }

    protected void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    protected void setSecret(boolean secret) {
        this.secret = secret;
    }

    protected void setChannelPrivate(boolean channelPrivate) {
        this.channelPrivate = channelPrivate;
    }

    protected void setTopicProtection(boolean topicProtection) {
        this.topicProtection = topicProtection;
    }

    protected void setChannelLimit(int channelLimit) {
        this.channelLimit = channelLimit;
    }

    protected void setModeStale(boolean modeStale) {
        this.modeStale = modeStale;
    }

    protected void setModeLatch(CountDownLatch modeLatch) {
        this.modeLatch = modeLatch;
    }

    protected void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

    public OutputChannel send() {
        try {
            return output.get();
        } catch (ConcurrentException ex) {
            throw new RuntimeException("Could not generate OutputChannel for " + getName(), ex);
        }
    }

    protected void parseMode(String rawMode) {
        if (rawMode.contains(" ")) {
            modeStale = true;
            return;
        }
        boolean adding = true;
        for (char curChar : rawMode.toCharArray()) {
            if (curChar == '-') {
                adding = false;
            } else if (curChar == '+') {
                adding = true;
            } else if (adding) {
                mode = mode + curChar;
            } else {
                mode = mode.replace(Character.toString(curChar), "");
            }
        }
    }

    public String getMode() {
        if (!modeStale) {
            return mode;
        }
        try {
            PircBotY.getLogger().log(Level.FINE, "Mode is stale for channel " + getName() + ", fetching fresh mode");
            if (modeLatch == null || modeLatch.getCount() == 0) {
                modeLatch = new CountDownLatch(1);
            }
            bot.sendRaw().rawLine("MODE " + getName());
            modeLatch.await();
            return mode;
        } catch (InterruptedException e) {
            throw new RuntimeException("Waiting for mode response interrupted", e);
        }
    }

    public boolean hasTopicProtection() {
        return topicProtection;
    }

    public ImmutableSortedSet<UserLevel> getUserLevels(User user) {
        return getDao().getLevels(this, user);
    }

    public ImmutableSortedSet<User> getNormalUsers() {
        return getDao().getNormalUsers(this);
    }

    public ImmutableSortedSet<User> getOps() {
        return getDao().getUsers(this, UserLevel.OP);
    }

    public ImmutableSortedSet<User> getVoices() {
        return getDao().getUsers(this, UserLevel.VOICE);
    }

    public ImmutableSortedSet<User> getOwners() {
        return getDao().getUsers(this, UserLevel.OWNER);
    }

    public ImmutableSortedSet<User> getHalfOps() {
        return getDao().getUsers(this, UserLevel.HALFOP);
    }

    public ImmutableSortedSet<User> getSuperOps() {
        return getDao().getUsers(this, UserLevel.SUPEROP);
    }

    protected void setMode(String mode, ImmutableList<String> modeParsed) {
        this.mode = mode;
        this.modeStale = false;
        if (modeLatch != null) {
            modeLatch.countDown();
        }
        PeekingIterator<String> params = Iterators.peekingIterator(modeParsed.iterator());
        boolean adding = true;
        String modeLetters = params.next();
        for (int i = 0; i < modeLetters.length(); i++) {
            char curModeChar = modeLetters.charAt(i);
            if (curModeChar == '+') {
                adding = true;
            } else if (curModeChar == '-') {
                adding = false;
            } else {
                ChannelModeHandler modeHandler = bot.getConfiguration().getChannelModeHandlers().get(curModeChar);
                if (modeHandler != null) {
                    modeHandler.handleMode(bot, this, null, params, adding, false);
                }
            }
        }
    }

    public ImmutableSortedSet<User> getUsers() {
        return getDao().getUsers(this);
    }

    public String getTopicSetter() {
        return topicSetter;
    }

    public boolean isOp(User user) {
        return getDao().levelContainsUser(UserLevel.OP, this, user);
    }

    public boolean hasVoice(User user) {
        return getDao().levelContainsUser(UserLevel.VOICE, this, user);
    }

    public boolean isSuperOp(User user) {
        return getDao().levelContainsUser(UserLevel.SUPEROP, this, user);
    }

    public boolean isOwner(User user) {
        return getDao().levelContainsUser(UserLevel.OWNER, this, user);
    }

    public boolean isHalfOp(User user) {
        return getDao().levelContainsUser(UserLevel.HALFOP, this, user);
    }

    public ChannelSnapshot createSnapshot() {
        if (modeStale) {
            PircBotY.getLogger().log(Level.WARNING, "Channel {0} mode '{1}' is stale", new Object[]{getName(), mode});
        }
        return new ChannelSnapshot(this, mode);
    }

    @Override
    public int compareTo(Channel other) {
        return getName().compareToIgnoreCase(other.getName());
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof Channel) {
            Channel c = (Channel) another;
            return c.getName().equals(getName()) && c.getBot().equals(getBot());
        }
        return false;
    }
}

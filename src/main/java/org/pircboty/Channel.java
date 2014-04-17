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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.pircboty.hooks.managers.ThreadedListenerManager;
import org.pircboty.output.OutputChannel;
import org.pircboty.snapshot.ChannelSnapshot;

/**
 * Represents a Channel that we're joined to.
 *
 * @author
 */
public class Channel implements Comparable<Channel> {

    /**
     * The name of the channel. Will never change
     */
    private final String name;
    /**
     * Unique UUID for this channel <i>instance</i>
     */
    private final UUID channelId = UUID.randomUUID();
    private final UserChannelDao dao;
    /**
     * The bot that this channel came from
     */
    private final PircBotY bot;
    //Output is lazily created since it might not ever be used
    private final AtomicSafeInitializer<OutputChannel> output = new AtomicSafeInitializer<OutputChannel>() {
        @Override
        protected OutputChannel initialize() {
            return bot.getConfiguration().getBotFactory().createOutputChannel(bot, Channel.this);
        }
    };
    private String mode = "";
    /**
     * The current channel topic
     */
    private String topic = "";
    /**
     * Timestamp of when the topic was created. Defaults to 0
     */
    private long topicTimestamp;
    /**
     * Timestamp of when channel was created. Defaults to 0
     */
    private long createTimestamp;
    /**
     * The user who set the topic. Default is blank
     */
    private String topicSetter = "";
    /**
     * Moderated (+m) status
     */
    private boolean moderated = false;
    /**
     * No external messages (+n) status
     */
    private boolean noExternalMessages = false;
    /**
     * Invite only (+i) status
     */
    private boolean inviteOnly = false;
    /**
     * Secret (+s) status
     */
    private boolean secret = false;
    /**
     * Private (+p) status
     */
    private boolean channelPrivate = false;
    private boolean topicProtection = false;
    /**
     * Channel limit (+l #)
     */
    private int channelLimit = -1;
    /**
     * Channel key (+k)
     */
    private String channelKey = null;
    private boolean modeStale = false;
    private CountDownLatch modeLatch = null;

    public Channel(PircBotY bot, UserChannelDao dao, String name) {
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

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTopicTimestamp(long topicTimestamp) {
        this.topicTimestamp = topicTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public void setTopicSetter(String topicSetter) {
        this.topicSetter = topicSetter;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public void setNoExternalMessages(boolean noExternalMessages) {
        this.noExternalMessages = noExternalMessages;
    }

    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }

    public void setChannelPrivate(boolean channelPrivate) {
        this.channelPrivate = channelPrivate;
    }

    public void setTopicProtection(boolean topicProtection) {
        this.topicProtection = topicProtection;
    }

    public void setChannelLimit(int channelLimit) {
        this.channelLimit = channelLimit;
    }

    public void setModeStale(boolean modeStale) {
        this.modeStale = modeStale;
    }

    public void setModeLatch(CountDownLatch modeLatch) {
        this.modeLatch = modeLatch;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }

    /**
     * Send a line to the channel.
     *
     * @return A {@link OutputChannel} for this channel
     */
    public OutputChannel send() {
        try {
            return output.get();
        } catch (ConcurrentException ex) {
            throw new RuntimeException("Could not generate OutputChannel for " + getName(), ex);
        }
    }

    public void parseMode(String rawMode) {
        if (rawMode.contains(" ")) {
            //Mode contains arguments which are impossible to parse.
            //Could be a ban command (we shouldn't use this), channel key (should, but where), etc
            //Need to ask server
            modeStale = true;
            return;
        }
        //Parse mode by switching between removing and adding by the existance of a + or - sign
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

    /**
     * Gets the channel mode. If mode is simple (no arguments), this will return
     * immediately. If its not (mode with arguments, eg channel key), then asks
     * the server for the correct mode, waiting until it gets a response
     * <p>
     * <b>WARNING:</b> Because of the last checking, a threaded listener manager
     * like {@link ThreadedListenerManager} is required. Using a single threaded
     * listener manager like
     * {@link org.PircBotY.hooks.managers.GenericListenerManager} will mean this
     * method <i>never returns</i>!
     *
     * @return A known good mode, either immediately or soon.
     */
    public String getMode() {
        if (!modeStale) {
            return mode;
        }
        //Mode is stale, get new mode from server
        try {
            PircBotY.getLogger().log(Level.FINE, "Mode is stale for channel " + getName() + ", fetching fresh mode");
            if (modeLatch == null || modeLatch.getCount() == 0) {
                modeLatch = new CountDownLatch(1);
            }
            bot.sendRaw().rawLine("MODE " + getName());
            //Wait for setMode to be called
            modeLatch.await();
            //We have known good mode from server, now return
            return mode;
        } catch (InterruptedException e) {
            throw new RuntimeException("Waiting for mode response interrupted", e);
        }
    }

    /**
     * Check if the channel has topic protection (+t) set.
     *
     * @return True if +t
     */
    public boolean hasTopicProtection() {
        return topicProtection;
    }

    /**
     * Get all levels the user holds in this channel.
     *
     * @param user The user to get the levels of
     * @return An <b>immutable copy</b> of the levels the user holds
     */
    public ImmutableSortedSet<UserLevel> getUserLevels(User user) {
        return getDao().getLevels(this, user);
    }

    /**
     * Get all users that don't have any special status in this channel. This
     * means that they aren't ops, have voice, superops, halops, or owners in
     * this channel
     *
     * @return An <b>immutable copy</b> of normal users
     */
    public ImmutableSortedSet<User> getNormalUsers() {
        return getDao().getNormalUsers(this);
    }

    /**
     * Get all opped users in this channel.
     *
     * @return An <b>immutable copy</b> of opped users
     */
    public ImmutableSortedSet<User> getOps() {
        return getDao().getUsers(this, UserLevel.OP);
    }

    /**
     * Get all voiced users in this channel.
     *
     * @return An <b>immutable copy</b> of voiced users
     */
    public ImmutableSortedSet<User> getVoices() {
        return getDao().getUsers(this, UserLevel.VOICE);
    }

    /**
     * Get all users with Owner status in this channel.
     *
     * @return An <b>immutable copy</b> of users with Owner status
     */
    public ImmutableSortedSet<User> getOwners() {
        return getDao().getUsers(this, UserLevel.OWNER);
    }

    /**
     * Get all users with Half Operator status in this channel.
     *
     * @return An <b>immutable copy</b> of users with Half Operator status
     */
    public ImmutableSortedSet<User> getHalfOps() {
        return getDao().getUsers(this, UserLevel.HALFOP);
    }

    /**
     * Get all users with Super Operator status in this channel.
     *
     * @return An <b>immutable copy</b> of users with Super Operator status
     */
    public ImmutableSortedSet<User> getSuperOps() {
        return getDao().getUsers(this, UserLevel.SUPEROP);
    }

    /**
     * Sets the mode of the channel. If there is a getMode() waiting on this,
     * fire it.
     *
     * @param mode
     */
    public void setMode(String mode, ImmutableList<String> modeParsed) {
        this.mode = mode;
        this.modeStale = false;
        if (modeLatch != null) {
            modeLatch.countDown();
        }
        //Parse out mode
        PeekingIterator<String> params = Iterators.peekingIterator(modeParsed.iterator());
        //Process modes letter by letter, grabbing paramaters as needed
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

    /**
     * Get all users in this channel. Simply calls {@link PircBotY#getUsers(org.PircBotY.Channel)
     * }
     *
     * @return An <i>Unmodifiable</i> Set of users in this channel
     */
    public ImmutableSortedSet<User> getUsers() {
        return getDao().getUsers(this);
    }

    /**
     * Get the user that set the topic. As the user may or may not be in the
     * channel return as a string
     *
     * @return The user that set the topic in String format
     */
    public String getTopicSetter() {
        return topicSetter;
    }

    /**
     * Checks if the given user is an Operator in this channel
     *
     * @return True if the user is an Operator, false if not
     */
    public boolean isOp(User user) {
        return getDao().levelContainsUser(UserLevel.OP, this, user);
    }

    /**
     * Checks if the given user has Voice in this channel.
     *
     * @return True if the user has Voice, false if not
     */
    public boolean hasVoice(User user) {
        return getDao().levelContainsUser(UserLevel.VOICE, this, user);
    }

    /**
     * Checks if the given user is a Super Operator in this channel.
     *
     * @return True if the user is a Super Operator, false if not
     */
    public boolean isSuperOp(User user) {
        return getDao().levelContainsUser(UserLevel.SUPEROP, this, user);
    }

    /**
     * Checks if the given user is an Owner in this channel.
     *
     * @return True if the user is an Owner, false if not
     */
    public boolean isOwner(User user) {
        return getDao().levelContainsUser(UserLevel.OWNER, this, user);
    }

    /**
     * Checks if the given user is a Half Operator in this channel.
     *
     * @return True if the user is a Half Operator, false if not
     */
    public boolean isHalfOp(User user) {
        return getDao().levelContainsUser(UserLevel.HALFOP, this, user);
    }

    /**
     * Create an immutable snapshot of this channel.
     *
     * @return Immutable Channel copy minus the DAO
     */
    public ChannelSnapshot createSnapshot() {
        if (modeStale) {
            PircBotY.getLogger().log(Level.WARNING, "Channel {0} mode '{1}' is stale", new Object[]{getName(), mode});
        }
        return new ChannelSnapshot(this, mode);
    }

    /**
     * Compare {@link #getName()} with {@link String#compareToIgnoreCase(java.lang.String)
     * }. This is useful for sorting lists of Channel objects.
     *
     * @param other Other channel to compare to
     * @return the result of calling compareToIgnoreCase on channel names.
     */
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

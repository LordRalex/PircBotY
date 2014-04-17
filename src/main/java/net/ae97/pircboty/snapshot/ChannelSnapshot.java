package net.ae97.pircboty.snapshot;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.UserChannelDao;

public class ChannelSnapshot extends Channel {

    private UserChannelDao<? extends PircBotY, UserSnapshot, ChannelSnapshot> dao;
    private final Channel generatedFrom;
    private final String mode;

    public ChannelSnapshot(Channel channel, String mode) {
        super(channel.getBot(), null, channel.getName());
        this.generatedFrom = channel;
        this.mode = mode;
        super.setCreateTimestamp(channel.getCreateTimestamp());
        super.setTopic(channel.getTopic());
        super.setTopicSetter(channel.getTopicSetter());
        super.setTopicTimestamp(channel.getTopicTimestamp());
        super.setChannelKey(channel.getChannelKey());
        super.setChannelLimit(channel.getChannelLimit());
        super.setChannelPrivate(channel.isChannelPrivate());
        super.setInviteOnly(channel.isInviteOnly());
        super.setModerated(channel.isModerated());
        super.setNoExternalMessages(channel.isNoExternalMessages());
        super.setSecret(channel.isSecret());
        super.setTopicProtection(channel.hasTopicProtection());
    }

    @Override
    public UserChannelDao<? extends PircBotY, UserSnapshot, ChannelSnapshot> getDao() {
        return dao;
    }

    @Override
    public ChannelSnapshot createSnapshot() {
        throw new UnsupportedOperationException("Attempting to generate channel snapshot from a snapshot");
    }

    @Override
    public void setTopic(String topic) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setTopicTimestamp(long topicTimestamp) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setCreateTimestamp(long createTimestamp) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setTopicSetter(String topicSetter) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setModerated(boolean moderated) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setNoExternalMessages(boolean noExternalMessages) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setInviteOnly(boolean inviteOnly) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setSecret(boolean secret) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setChannelPrivate(boolean channelPrivate) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setTopicProtection(boolean topicProtection) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setChannelLimit(int channelLimit) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    @Override
    public void setChannelKey(String channelKey) {
        throw new UnsupportedOperationException("Cannot change settings on snapshot");
    }

    public Channel getGeneratedFrom() {
        return generatedFrom;
    }

    public void setDao(UserChannelDao<? extends PircBotY, UserSnapshot, ChannelSnapshot> dao) {
        this.dao = dao;
    }
}

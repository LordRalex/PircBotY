package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;

public class TopicEvent<T extends PircBotY> extends Event<T> implements GenericChannelUserEvent<T> {

    private final Channel channel;
    private final String oldTopic;
    private final String topic;
    private final User user;
    private final boolean changed;
    private final long date;

    public TopicEvent(T bot, Channel channel, String oldTopic, String topic, User user, long date, boolean changed) {
        super(bot);
        this.channel = channel;
        this.oldTopic = oldTopic;
        this.topic = topic;
        this.user = user;
        this.changed = changed;
        this.date = date;
    }

    @Override
    public void respond(String response) {
        getChannel().send().message(getUser(), response);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return user;
    }
}

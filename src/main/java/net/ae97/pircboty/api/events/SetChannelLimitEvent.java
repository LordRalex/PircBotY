package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelModeEvent;

public class SetChannelLimitEvent extends Event implements GenericChannelModeEvent {

    private final Channel channel;
    private final User user;
    private final int limit;

    public SetChannelLimitEvent(PircBotY bot, Channel channel, User user, int limit) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.limit = limit;
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

    public int getLimit() {
        return limit;
    }
}

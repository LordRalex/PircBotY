package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelModeEvent;

public class RemoveChannelKeyEvent extends Event implements GenericChannelModeEvent {

    private final Channel channel;
    private final User user;
    private final String key;

    public RemoveChannelKeyEvent(PircBotY bot, Channel channel, User user, String key) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.key = key;
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

    public String getKey() {
        return key;
    }
}

package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;

public class JoinEvent extends Event implements GenericChannelUserEvent {

    private final Channel channel;
    private final User user;

    public JoinEvent(PircBotY bot, Channel channel, User user) {
        super(bot);
        this.channel = channel;
        this.user = user;
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

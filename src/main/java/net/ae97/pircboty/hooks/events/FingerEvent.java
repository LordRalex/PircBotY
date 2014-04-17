package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;

public class FingerEvent<T extends PircBotY> extends Event<T> implements GenericChannelUserEvent<T> {

    private final User user;
    private final Channel channel;

    public FingerEvent(T bot, User user, Channel channel) {
        super(bot);
        this.user = user;
        this.channel = channel;
    }

    @Override
    public void respond(String response) {
        getUser().send().ctcpResponse(response);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }
}

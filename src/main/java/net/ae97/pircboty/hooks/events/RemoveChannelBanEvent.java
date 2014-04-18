package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelModeEvent;

public class RemoveChannelBanEvent extends Event implements GenericChannelModeEvent {

    private final Channel channel;
    private final User user;
    private final String hostmask;

    public RemoveChannelBanEvent(PircBotY bot, Channel channel, User user, String hostmask) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.hostmask = hostmask;
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

    public String getHostmask() {
        return hostmask;
    }
}

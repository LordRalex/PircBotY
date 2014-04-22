package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelModeEvent;

public class SetChannelBanEvent extends Event implements GenericChannelModeEvent {

    private final Channel channel;
    private final User user;
    private final String hostmask;

    public SetChannelBanEvent(PircBotY bot, Channel channel, User user, String hostmask) {
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

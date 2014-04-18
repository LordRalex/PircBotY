package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericCTCPEvent;

public class TimeEvent extends Event implements GenericCTCPEvent {

    private final Channel channel;
    private final User user;

    public TimeEvent(PircBotY bot, Channel channel, User user) {
        super(bot);
        this.channel = channel;
        this.user = user;
    }

    @Override
    public void respond(String response) {
        getUser().send().ctcpResponse(response);
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

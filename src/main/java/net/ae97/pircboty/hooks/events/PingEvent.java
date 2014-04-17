package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericCTCPEvent;

public class PingEvent<T extends PircBotY> extends Event<T> implements GenericCTCPEvent<T> {

    private final User user;
    private final Channel channel;
    private final String pingValue;

    public PingEvent(T bot, User user, Channel channel, String pingValue) {
        super(bot);
        this.user = user;
        this.channel = channel;
        this.pingValue = pingValue;
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

    public String getPingValue() {
        return pingValue;
    }
}

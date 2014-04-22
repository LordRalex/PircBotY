package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelUserEvent;

public class KickEvent extends Event implements GenericChannelUserEvent {

    private final Channel channel;
    private final User user;
    private final User recipient;
    private final String reason;

    public KickEvent(PircBotY bot, Channel channel, User user, User recipient, String reason) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.recipient = recipient;
        this.reason = reason;
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

    public User getRecipient() {
        return recipient;
    }

    public String getReason() {
        return reason;
    }
}

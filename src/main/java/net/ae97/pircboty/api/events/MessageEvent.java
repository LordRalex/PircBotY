package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelUserEvent;
import net.ae97.pircboty.generics.GenericMessageEvent;

public class MessageEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {

    private final Channel channel;
    private final User user;
    private final String message;

    public MessageEvent(PircBotY bot, Channel channel, User user, String message) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.message = message;
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

    @Override
    public String getMessage() {
        return message;
    }
}

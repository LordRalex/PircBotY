package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;
import net.ae97.pircboty.hooks.types.GenericMessageEvent;

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

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}

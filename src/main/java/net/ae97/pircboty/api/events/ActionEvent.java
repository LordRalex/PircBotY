package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelUserEvent;
import net.ae97.pircboty.generics.GenericMessageEvent;

public class ActionEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {

    private final User user;
    private final Channel channel;
    private final String action;

    public ActionEvent(PircBotY bot, User user, Channel channel, String action) {
        super(bot);
        this.user = user;
        this.channel = channel;
        this.action = action;
    }

    @Override
    public String getMessage() {
        return action;
    }

    @Override
    public void respond(String response) {
        if (getChannel() == null) {
            getUser().send().action(response);
        } else {
            getChannel().send().action(response);
        }
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

package net.ae97.pircboty.api.events;

import java.util.Set;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelEvent;

public class UserListEvent extends Event implements GenericChannelEvent {

    private final Channel channel;
    private final Set<User> users;

    public UserListEvent(PircBotY bot, Channel channel, Set<User> users) {
        super(bot);
        this.channel = channel;
        this.users = users;
    }

    @Override
    public void respond(String response) {
        getChannel().send().message(response);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public Set<User> getUsers() {
        return users;
    }
}

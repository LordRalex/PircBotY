package net.ae97.pircboty.hooks.events;

import com.google.common.collect.ImmutableSortedSet;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelEvent;

public class UserListEvent<B extends PircBotY> extends Event<B> implements GenericChannelEvent<B> {

    private final Channel channel;
    private final ImmutableSortedSet<User> users;

    public UserListEvent(B bot, Channel channel, ImmutableSortedSet<User> users) {
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

    public ImmutableSortedSet<User> getUsers() {
        return users;
    }
}

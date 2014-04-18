package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.api.users.User;

public class JoinEvent implements UserEvent, ChannelEvent, CancellableEvent {

    private final Channel channel;
    private final User sender;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public JoinEvent(net.ae97.pircboty.hooks.events.JoinEvent event) {
        channel = PokeBot.getChannel(event.getChannel().getName());
        sender = PokeBot.getUser(event.getUser().getNick());
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return sender;
    }

    @Override
    public void setCancelled(boolean state) {
        isCancelled = state;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}

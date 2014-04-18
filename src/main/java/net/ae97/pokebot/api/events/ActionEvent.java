package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.api.users.User;

public class ActionEvent implements UserEvent, ChannelEvent, CancellableEvent {

    private final Channel channel;
    private final User sender;
    private final String action;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public ActionEvent(net.ae97.pircboty.hooks.events.ActionEvent event) {
        channel = PokeBot.getChannel(event.getChannel().getName());
        sender = PokeBot.getUser(event.getUser().getNick());
        action = event.getMessage();
    }

    public String getAction() {
        return action;
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

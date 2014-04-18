package net.ae97.pokebot.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.api.users.User;

public class PartEvent implements CancellableEvent, UserEvent, ChannelEvent {

    private final User sender;
    private final Channel channel;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public PartEvent(net.ae97.pircboty.hooks.events.PartEvent event) {
        channel = PokeBot.getChannel(event.getChannel().getName());
        sender = PokeBot.getUser(event.getUser().getNick());
    }

    public PartEvent(PircBotY bot, net.ae97.pircboty.User s, net.ae97.pircboty.Channel c) {
        channel = PokeBot.getChannel(c.getName());
        sender = PokeBot.getUser(s.getNick());
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

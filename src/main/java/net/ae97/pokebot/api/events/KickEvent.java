package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.api.users.User;

public class KickEvent implements CancellableEvent, UserEvent, ChannelEvent {

    private final User kicker;
    private final User user;
    private final Channel channel;
    private final String message;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public KickEvent(net.ae97.pircboty.hooks.events.KickEvent event) {
        channel = PokeBot.getChannel(event.getChannel().getName());
        kicker = PokeBot.getUser(event.getUser().getNick());
        user = PokeBot.getUser(event.getRecipient().getNick());
        message = event.getReason();
    }

    public User getKicker() {
        return kicker;
    }

    public String getMessage() {
        return message;
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

package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.users.User;

public class QuitEvent implements UserEvent, CancellableEvent {

    private final User sender;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public QuitEvent(net.ae97.pircboty.hooks.events.QuitEvent event) {
        sender = PokeBot.getUser(event.getUser().getNick());
    }

    public User getSender() {
        return sender;
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

package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.users.User;

public class NickChangeEvent implements UserEvent, CancellableEvent {

    private final String oldNick, newNick;
    private final User sender;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public NickChangeEvent(net.ae97.pircboty.hooks.events.NickChangeEvent event) {
        oldNick = event.getOldNick();
        newNick = event.getNewNick();
        sender = PokeBot.getUser(event.getUser().getNick());
    }

    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
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

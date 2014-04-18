package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.users.User;

public class NoticeEvent implements UserEvent, CancellableEvent {

    private final String message;
    private final User sender;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public NoticeEvent(net.ae97.pircboty.hooks.events.NoticeEvent event) {
        sender = PokeBot.getUser(event.getUser().getNick());
        message = event.getMessage();
    }

    public String getMessage() {
        return message;
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

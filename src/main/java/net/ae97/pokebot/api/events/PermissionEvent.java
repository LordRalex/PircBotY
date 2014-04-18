package net.ae97.pokebot.api.events;

import net.ae97.pokebot.api.users.User;

public class PermissionEvent implements UserEvent {

    private final User user;
    private final boolean isForced;
    private final long timestamp = System.currentTimeMillis();

    public PermissionEvent(User u) {
        this(u, false);
    }

    public PermissionEvent(User u, boolean isF) {
        user = u;
        isForced = isF;
    }

    @Override
    public User getUser() {
        return user;
    }

    public boolean isForced() {
        return isForced;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}

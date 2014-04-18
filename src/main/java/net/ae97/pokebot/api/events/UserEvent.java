package net.ae97.pokebot.api.events;

import net.ae97.pokebot.api.users.User;

public interface UserEvent extends Event {

    public User getUser();
}

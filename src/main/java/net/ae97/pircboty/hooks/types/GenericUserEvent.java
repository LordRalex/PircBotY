package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.User;

public interface GenericUserEvent extends GenericEvent {

    public User getUser();
}

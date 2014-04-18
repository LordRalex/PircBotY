package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.User;

public interface GenericUserModeEvent extends GenericChannelUserEvent {

    public User getRecipient();
}

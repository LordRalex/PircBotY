package net.ae97.pircboty.generics;

import net.ae97.pircboty.User;

public interface GenericUserModeEvent extends GenericChannelUserEvent {

    public User getRecipient();
}

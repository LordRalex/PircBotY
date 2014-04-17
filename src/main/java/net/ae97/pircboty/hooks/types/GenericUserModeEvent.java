package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;

public interface GenericUserModeEvent<T extends PircBotY> extends GenericChannelUserEvent<T> {

    public User getRecipient();
}

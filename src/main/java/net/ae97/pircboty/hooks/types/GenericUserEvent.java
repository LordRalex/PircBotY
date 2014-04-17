package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;

public interface GenericUserEvent<T extends PircBotY> extends GenericEvent<T> {

    public User getUser();
}

package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;

public interface GenericMessageEvent<T extends PircBotY> extends GenericUserEvent<T> {

    public String getMessage();
}

package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;

public interface GenericDCCEvent<T extends PircBotY> extends GenericUserEvent<T> {

    public boolean isPassive();
}

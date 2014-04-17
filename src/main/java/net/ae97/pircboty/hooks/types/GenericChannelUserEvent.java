package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;

public interface GenericChannelUserEvent<T extends PircBotY> extends GenericUserEvent<T>, GenericChannelEvent<T> {
}

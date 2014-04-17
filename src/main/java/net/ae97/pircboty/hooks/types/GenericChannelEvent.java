package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;

public interface GenericChannelEvent<T extends PircBotY> extends GenericEvent<T> {

    public Channel getChannel();
}

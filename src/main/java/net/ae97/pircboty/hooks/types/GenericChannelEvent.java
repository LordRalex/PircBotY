package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.Channel;

public interface GenericChannelEvent extends GenericEvent {

    public Channel getChannel();
}

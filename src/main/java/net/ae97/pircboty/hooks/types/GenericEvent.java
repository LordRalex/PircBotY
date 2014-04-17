package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public interface GenericEvent<T extends PircBotY> extends Comparable<Event<T>> {

    public void respond(String response);

    public T getBot();

    public long getTimestamp();
}

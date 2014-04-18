package net.ae97.pircboty.hooks.types;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public interface GenericEvent extends Comparable<Event> {

    public void respond(String response);

    public PircBotY getBot();

    public long getTimestamp();
}

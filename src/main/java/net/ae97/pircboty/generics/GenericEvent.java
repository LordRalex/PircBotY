package net.ae97.pircboty.generics;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;

public interface GenericEvent extends Comparable<Event> {

    public void respond(String response);

    public PircBotY getBot();

    public long getTimestamp();
}

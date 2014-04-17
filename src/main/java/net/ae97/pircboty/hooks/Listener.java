package net.ae97.pircboty.hooks;

import net.ae97.pircboty.PircBotY;

public interface Listener<T extends PircBotY> {

    public void onEvent(Event<T> event) throws Exception;
}

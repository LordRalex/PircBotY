package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class ConnectEvent<T extends PircBotY> extends Event<T> {

    public ConnectEvent(T bot) {
        super(bot);
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }
}

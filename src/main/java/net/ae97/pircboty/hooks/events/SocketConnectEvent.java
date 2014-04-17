package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class SocketConnectEvent<T extends PircBotY> extends Event<T> {

    public SocketConnectEvent(T bot) {
        super(bot);
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }
}

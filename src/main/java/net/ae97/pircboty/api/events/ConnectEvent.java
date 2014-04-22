package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;

public class ConnectEvent extends Event {

    public ConnectEvent(PircBotY bot) {
        super(bot);
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }
}

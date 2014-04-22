package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;

public class ServerPingEvent extends Event {

    private final String response;

    public ServerPingEvent(PircBotY bot, String response) {
        super(bot);
        this.response = response;
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public String getResponse() {
        return response;
    }
}

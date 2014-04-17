package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class ServerPingEvent<T extends PircBotY> extends Event<T> {

    private final String response;

    public ServerPingEvent(T bot, String response) {
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

package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class UnknownEvent extends Event {

    private final String line;

    public UnknownEvent(PircBotY bot, String line) {
        super(bot);
        this.line = line;
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public String getLine() {
        return line;
    }
}

package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class UnknownEvent<T extends PircBotY> extends Event<T> {

    private final String line;

    public UnknownEvent(T bot, String line) {
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

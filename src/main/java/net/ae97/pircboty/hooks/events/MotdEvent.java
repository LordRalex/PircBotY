package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class MotdEvent<T extends PircBotY> extends Event<T> {

    private final String motd;

    public MotdEvent(T bot, String motd) {
        super(bot);
        this.motd = motd;
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public String getMotd() {
        return motd;
    }
}

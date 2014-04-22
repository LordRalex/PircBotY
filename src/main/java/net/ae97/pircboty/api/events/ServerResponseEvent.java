package net.ae97.pircboty.api.events;

import java.util.List;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;

public class ServerResponseEvent extends Event {

    private final int code;
    private final String rawLine;
    private final List<String> parsedResponse;

    public ServerResponseEvent(PircBotY bot, int code, String rawLine, List<String> parsedResponse) {
        super(bot);
        this.code = code;
        this.rawLine = rawLine;
        this.parsedResponse = parsedResponse;
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public int getCode() {
        return code;
    }

    public String getRawLine() {
        return rawLine;
    }

    public List<String> getParsedResponse() {
        return parsedResponse;
    }
}

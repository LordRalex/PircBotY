package net.ae97.pircboty.hooks.events;

import com.google.common.collect.ImmutableList;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class ServerResponseEvent<T extends PircBotY> extends Event<T> {

    private final int code;
    private final String rawLine;
    private final ImmutableList<String> parsedResponse;

    public ServerResponseEvent(T bot, int code, String rawLine, ImmutableList<String> parsedResponse) {
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

    public ImmutableList<String> getParsedResponse() {
        return parsedResponse;
    }
}

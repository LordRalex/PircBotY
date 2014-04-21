package net.ae97.pircboty;

import java.util.Iterator;

public abstract class ChannelModeHandler {

    private final char mode;

    public ChannelModeHandler(char mode) {
        this.mode = mode;
    }

    public char getMode() {
        return mode;
    }

    public abstract void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent);
}

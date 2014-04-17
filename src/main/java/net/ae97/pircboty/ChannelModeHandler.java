package net.ae97.pircboty;

import com.google.common.collect.PeekingIterator;

public abstract class ChannelModeHandler {

    private final char mode;

    public ChannelModeHandler(char mode) {
        this.mode = mode;
    }

    public char getMode() {
        return mode;
    }

    public abstract void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent);
}

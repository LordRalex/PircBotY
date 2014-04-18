package net.ae97.pircboty.hooks.events;

import com.google.common.collect.ImmutableList;
import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelModeEvent;

public class ModeEvent extends Event implements GenericChannelModeEvent {

    private final Channel channel;
    private final User user;
    private final String mode;
    private final ImmutableList<String> modeParsed;

    public ModeEvent(PircBotY bot, Channel channel, User user, String mode, ImmutableList<String> modeParsed) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.mode = mode;
        this.modeParsed = modeParsed;
    }

    @Override
    public void respond(String response) {
        getChannel().send().message(getUser(), response);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return user;
    }

    public String getMode() {
        return mode;
    }

    public ImmutableList<String> getModeParsed() {
        return modeParsed;
    }
}

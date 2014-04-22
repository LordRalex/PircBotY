package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericUserModeEvent;

public class VoiceEvent extends Event implements GenericUserModeEvent {

    private final Channel channel;
    private final User user;
    private final User recipient;
    private final boolean hasVoice;

    public VoiceEvent(PircBotY bot, Channel channel, User user, User recipient, boolean isVoice) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.recipient = recipient;
        this.hasVoice = isVoice;
    }

    @Override
    public void respond(String response) {
        getChannel().send().message(response);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public User getRecipient() {
        return recipient;
    }

    public boolean isVoiced() {
        return hasVoice;
    }
}

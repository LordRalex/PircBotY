package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericUserModeEvent;

public class OwnerEvent<T extends PircBotY> extends Event<T> implements GenericUserModeEvent<T> {

    private final Channel channel;
    private final User user;
    private final User recipient;
    private final boolean isOwner;

    public OwnerEvent(T bot, Channel channel, User user, User recipient, boolean isOwner) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.recipient = recipient;
        this.isOwner = isOwner;
    }

    @Deprecated
    public boolean isFounder() {
        return isOwner;
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

    @Override
    public User getRecipient() {
        return recipient;
    }

    public boolean isIsOwner() {
        return isOwner;
    }
}

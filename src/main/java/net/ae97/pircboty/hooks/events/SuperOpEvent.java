package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericUserModeEvent;

public class SuperOpEvent<T extends PircBotY> extends Event<T> implements GenericUserModeEvent<T> {

    private final Channel channel;
    private final User user;
    private final User recipient;
    private final boolean isSuperOp;

    public SuperOpEvent(T bot, Channel channel, User user, User recipient, boolean isSuperOp) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.recipient = recipient;
        this.isSuperOp = isSuperOp;
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

    public User getRecipient() {
        return recipient;
    }

    public boolean isIsSuperOp() {
        return isSuperOp;
    }
}

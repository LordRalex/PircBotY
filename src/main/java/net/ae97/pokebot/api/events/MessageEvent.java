package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.api.recipients.MessageRecipient;
import net.ae97.pokebot.api.users.User;

public final class MessageEvent implements UserEvent, ChannelEvent, CancellableEvent, ReplyableEvent {

    private final String message;
    private final User sender;
    private final Channel channel;
    private boolean isCancelled = false;
    private final long timestamp = System.currentTimeMillis();

    public MessageEvent(net.ae97.pircboty.hooks.events.MessageEvent event) {
        channel = PokeBot.getChannel(event.getChannel().getName());
        sender = PokeBot.getUser(event.getUser().getNick());
        message = event.getMessage();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return sender;
    }

    @Override
    public void setCancelled(boolean state) {
        isCancelled = state;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void reply(String... messages) {
        MessageRecipient rec = channel == null ? sender : channel;
        if (rec == null) {
            return;
        }
        rec.sendMessage(messages);
    }
}

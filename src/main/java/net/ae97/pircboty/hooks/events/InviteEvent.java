package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class InviteEvent<T extends PircBotY> extends Event<T> {

    private final String user;
    private final String channel;

    public InviteEvent(T bot, String user, String channel) {
        super(bot);
        this.user = user;
        this.channel = channel;
    }

    @Override
    public void respond(String response) {
        getBot().sendIRC().message(getUser(), response);
    }

    public String getUser() {
        return user;
    }

    public String getChannel() {
        return channel;
    }
}

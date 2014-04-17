package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericMessageEvent;

public class PrivateMessageEvent<T extends PircBotY> extends Event<T> implements GenericMessageEvent<T> {

    private final User user;
    private final String message;

    public PrivateMessageEvent(T bot, User user, String message) {
        super(bot);
        this.user = user;
        this.message = message;
    }

    @Override
    public void respond(String response) {
        getUser().send().message(response);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

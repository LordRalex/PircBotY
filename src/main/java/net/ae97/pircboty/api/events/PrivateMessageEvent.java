package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericMessageEvent;

public class PrivateMessageEvent extends Event implements GenericMessageEvent {

    private final User user;
    private final String message;

    public PrivateMessageEvent(PircBotY bot, User user, String message) {
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

package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericUserEvent;

public class UserModeEvent extends Event implements GenericUserEvent {

    private final User user;
    private final User recipient;
    private final String mode;

    public UserModeEvent(PircBotY bot, User user, User recipient, String mode) {
        super(bot);
        this.user = user;
        this.recipient = recipient;
        this.mode = mode;
    }

    @Override
    public void respond(String response) {
        getUser().send().message(response);
    }

    @Override
    public User getUser() {
        return user;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getMode() {
        return mode;
    }
}

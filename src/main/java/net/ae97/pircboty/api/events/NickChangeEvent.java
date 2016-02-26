package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericUserEvent;

public class NickChangeEvent extends Event implements GenericUserEvent {

    private final String oldNick;
    private final String newNick;
    private final User user;

    public NickChangeEvent(PircBotY bot, String oldNick, String newNick, User user) {
        super(bot);
        this.oldNick = oldNick;
        this.newNick = newNick;
        this.user = user;
    }

    @Override
    public void respond(String response) {
        getUser().send().message(response);
    }

    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
    }

    @Override
    public User getUser() {
        return user;
    }
}

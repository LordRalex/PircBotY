package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericUserEvent;
import net.ae97.pircboty.snapshot.UserChannelDaoSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;

public class QuitEvent extends Event implements GenericUserEvent {

    private final UserChannelDaoSnapshot<PircBotY> daoSnapshot;
    private final UserSnapshot user;
    private final String reason;

    public QuitEvent(PircBotY bot, UserChannelDaoSnapshot<PircBotY> daoSnapshot, UserSnapshot user, String reason) {
        super(bot);
        this.daoSnapshot = daoSnapshot;
        this.user = user;
        this.reason = reason;
        user.setDao(daoSnapshot);
    }

    @Override
    public void respond(String response) {
        throw new UnsupportedOperationException("Attempting to respond to a user that quit");
    }

    public UserChannelDaoSnapshot<PircBotY> getDaoSnapshot() {
        return daoSnapshot;
    }

    @Override
    public UserSnapshot getUser() {
        return user;
    }

    public String getReason() {
        return reason;
    }
}

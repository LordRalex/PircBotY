package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.UserChannelDao;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericUserEvent;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;

public class QuitEvent extends Event implements GenericUserEvent {

    private final UserChannelDao<PircBotY, UserSnapshot, ChannelSnapshot> daoSnapshot;
    private final UserSnapshot user;
    private final String reason;

    public QuitEvent(PircBotY bot, UserChannelDao<PircBotY, UserSnapshot, ChannelSnapshot> daoSnapshot, UserSnapshot user, String reason) {
        super(bot);
        this.daoSnapshot = daoSnapshot;
        this.user = user;
        this.reason = reason;
    }

    @Override
    @Deprecated
    public void respond(String response) {
        throw new UnsupportedOperationException("Attempting to respond to a user that quit");
    }

    public UserChannelDao<PircBotY, UserSnapshot, ChannelSnapshot> getDaoSnapshot() {
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

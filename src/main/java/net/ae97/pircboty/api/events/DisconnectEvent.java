package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.UserChannelDao;
import net.ae97.pircboty.api.Event;

public class DisconnectEvent extends Event {

    private final UserChannelDao<PircBotY, ? extends User, ? extends Channel> daoSnapshot;
    private final Exception disconnectException;

    public DisconnectEvent(PircBotY bot, UserChannelDao<PircBotY, ? extends User, ? extends Channel> daoSnapshot, Exception disconnectException) {
        super(bot);
        this.daoSnapshot = daoSnapshot;
        this.disconnectException = disconnectException;
    }

    @Override
    @Deprecated
    public void respond(String response) {
        throw new UnsupportedOperationException("Attepting to respond to a disconnected server");
    }
}

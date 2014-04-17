package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.UserChannelDao;
import net.ae97.pircboty.hooks.Event;

public class DisconnectEvent<T extends PircBotY> extends Event<T> {

    private final UserChannelDao<T, ? extends User, ? extends Channel> daoSnapshot;
    private final Exception disconnectException;

    public DisconnectEvent(T bot, UserChannelDao<T, ? extends User, ? extends Channel> daoSnapshot, Exception disconnectException) {
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

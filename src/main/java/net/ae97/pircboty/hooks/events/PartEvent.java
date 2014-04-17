package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.UserChannelDao;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;

public class PartEvent<T extends PircBotY> extends Event<T> implements GenericChannelUserEvent<T> {

    private final UserChannelDao<T, UserSnapshot, ChannelSnapshot> daoSnapshot;
    private final ChannelSnapshot channel;
    private final UserSnapshot user;
    private final String reason;

    public PartEvent(T bot, UserChannelDao<T, UserSnapshot, ChannelSnapshot> daoSnapshot, ChannelSnapshot channel, UserSnapshot user, String reason) {
        super(bot);
        this.daoSnapshot = daoSnapshot;
        this.channel = channel;
        this.user = user;
        this.reason = reason;
    }

    @Override
    public void respond(String response) {
        getChannel().send().message(response);
    }

    public UserChannelDao<T, UserSnapshot, ChannelSnapshot> getDaoSnapshot() {
        return daoSnapshot;
    }

    @Override
    public ChannelSnapshot getChannel() {
        return channel;
    }

    @Override
    public UserSnapshot getUser() {
        return user;
    }

    public String getReason() {
        return reason;
    }
}

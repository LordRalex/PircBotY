package net.ae97.pircboty.api.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericChannelUserEvent;
import net.ae97.pircboty.generics.GenericMessageEvent;

public class NoticeEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {

    private final User user;
    private final Channel channel;
    private final String notice;

    public NoticeEvent(PircBotY bot, User user, Channel channel, String notice) {
        super(bot);
        this.user = user;
        this.channel = channel;
        this.notice = notice;
    }

    @Override
    public String getMessage() {
        return notice;
    }

    @Override
    public void respond(String response) {
        if (getChannel() == null) {
            getUser().send().message(response);
        } else {
            getChannel().send().message(getUser(), response);
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    public String getNotice() {
        return notice;
    }
}

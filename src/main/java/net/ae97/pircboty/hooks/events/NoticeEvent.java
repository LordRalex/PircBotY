package net.ae97.pircboty.hooks.events;

import net.ae97.pircboty.Channel;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericChannelUserEvent;
import net.ae97.pircboty.hooks.types.GenericMessageEvent;

public class NoticeEvent<T extends PircBotY> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T> {

    private final User user;
    private final Channel channel;
    private final String notice;

    public NoticeEvent(T bot, User user, Channel channel, String notice) {
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

/**
 * Copyright (C) 2010-2013
 *
 * This file is part of PircBotY.
 *
 * PircBotY is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotY is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotY. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircboty.hooks.events;

import org.pircboty.Channel;
import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericChannelUserEvent;

/**
 * This event is dispatched whenever a user sets the topic, or when we join a
 * new channel and discovers its topic.
 *
 * @author
 */
public class TopicEvent<T extends PircBotY> extends Event<T> implements GenericChannelUserEvent<T> {

    private final Channel channel;
    private final String oldTopic;
    private final String topic;
    private final User user;
    private final boolean changed;
    private final long date;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel that the topic belongs to.
     * @param topic The topic for the channel.
     * @param user The user that set the topic.
     * @param date When the topic was set (milliseconds since the epoch).
     * @param changed True if the topic has just been changed, false if the
     * topic was already there.
     */
    public TopicEvent(T bot, Channel channel, String oldTopic, String topic, User user, long date, boolean changed) {
        super(bot);
        this.channel = channel;
        this.oldTopic = oldTopic;
        this.topic = topic;
        this.user = user;
        this.changed = changed;
        this.date = date;
    }

    /**
     * Respond with a channel message in <code>user: message</code> format to
     * the user that set the message
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getChannel().send().message(getUser(), response);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return user;
    }
}

/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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

import javax.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.pircboty.Channel;
import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericChannelModeEvent;

/**
 * Called when a channel is set to 'invite only' mode. A user may only join the
 * channel if they are invited by someone who is already in the channel.
 * <p>
 * This is a type of mode change and therefor is also dispatched in a
 * {@link org.PircBotY.hooks.events.ModeEvent}
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SetInviteOnlyEvent<T extends PircBotY> extends Event<T> implements GenericChannelModeEvent<T> {

    @Getter(onMethod = @_(
            @Override))
    protected final Channel channel;
    @Getter(onMethod = @_(
            @Override))
    protected final User user;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel in which the mode change took place.
     * @param user The user that performed the mode change.
     */
    public SetInviteOnlyEvent(T bot, @NonNull Channel channel, @NonNull User user) {
        super(bot);
        this.channel = channel;
        this.user = user;
    }

    /**
     * Respond by send a message in the channel to the user that set the mode in
     * <code>user: message</code> format
     *
     * @param response The response to send
     */
    @Override
    public void respond(@Nullable String response) {
        getChannel().send().message(getUser(), response);
    }
}

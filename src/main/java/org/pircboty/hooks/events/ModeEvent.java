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

import com.google.common.collect.ImmutableList;
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
 * Used when the mode of a channel is set.
 * <p>
 * You may find it more convenient to decode the meaning of the mode string by
 * using instead {@link OpEvent}, {@link VoiceEvent},
 * {@link SetChannelKeyEvent}, {@link RemoveChannelKeyEvent},
 * {@link SetChannelLimitEvent}, {@link RemoveChannelLimitEvent},
 * {@link SetChannelBanEvent} or {@link RemoveChannelBanEvent} as appropriate.
 * <p>
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModeEvent<T extends PircBotY> extends Event<T> implements GenericChannelModeEvent<T> {

    @Getter(onMethod = @_(
            @Override))
    protected final Channel channel;
    @Getter(onMethod = @_(
            @Override))
    protected final User user;
    protected final String mode;
    protected final ImmutableList<String> modeParsed;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel that the mode operation applies to.
     * @param user The user that set the mode.
     * @param mode The mode that has been set.
     */
    public ModeEvent(T bot, @NonNull Channel channel, User user, @NonNull String mode, @NonNull ImmutableList<String> modeParsed) {
        super(bot);
        this.channel = channel;
        this.user = user;
        this.mode = mode;
        this.modeParsed = modeParsed;
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

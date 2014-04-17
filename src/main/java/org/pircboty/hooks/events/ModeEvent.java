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

import com.google.common.collect.ImmutableList;
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
 * @author
 */
public class ModeEvent<T extends PircBotY> extends Event<T> implements GenericChannelModeEvent<T> {

    private final Channel channel;
    private final User user;
    private final String mode;
    private final ImmutableList<String> modeParsed;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param channel The channel that the mode operation applies to.
     * @param user The user that set the mode.
     * @param mode The mode that has been set.
     */
    public ModeEvent(T bot, Channel channel, User user, String mode, ImmutableList<String> modeParsed) {
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

    public String getMode() {
        return mode;
    }

    public ImmutableList<String> getModeParsed() {
        return modeParsed;
    }
}

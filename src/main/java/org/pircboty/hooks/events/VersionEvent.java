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
import org.pircboty.hooks.CoreHooks;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.types.GenericCTCPEvent;
import org.pircboty.hooks.types.GenericChannelEvent;

/**
 * This event is dispatched whenever we receive a VERSION request.
 * <p>
 * {@link CoreHooks} automatically responds correctly. Unless {@link CoreHooks}
 * is removed from the
 * {@link PircBotY#getListenerManager() bot's ListenerManager}, Listeners of
 * this event should <b>not</b> send a response as the user will get two
 * responses
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VersionEvent<T extends PircBotY> extends Event<T> implements GenericCTCPEvent<T>, GenericChannelEvent<T> {

    @Getter(onMethod = @_({
        @Override}))
    protected final User user;
    @Getter(onMethod = @_({
        @Override}))
    protected final Channel channel;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param user The nick of the user that sent the VERSION request.
     * @param channel The target channel of the VERSION request. A value of
     * <code>null</code> means that that the target is us.
     */
    public VersionEvent(T bot, @NonNull User user, Channel channel) {
        super(bot);
        this.user = user;
        this.channel = channel;
    }

    /**
     * Respond with a CTCP response to the user
     *
     * @param response The response to send
     */
    @Override
    public void respond(@Nullable String response) {
        getUser().send().ctcpResponse(response);
    }
}

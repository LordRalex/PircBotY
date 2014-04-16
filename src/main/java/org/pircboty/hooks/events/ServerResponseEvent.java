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
import lombok.NonNull;
import org.pircboty.PircBotY;
import org.pircboty.ReplyConstants;
import org.pircboty.hooks.Event;

/**
 * This is called when we receive a numeric response from the IRC server.
 * <p>
 * Numerics in the range from 001 to 099 are used for client-server connections
 * only and should never travel between servers. Replies generated in response
 * to commands are found in the range from 200 to 399. Error replies are found
 * in the range from 400 to 599.
 * <p>
 * For example, we can use this event to discover the topic of a channel when we
 * join it. If we join the channel #test which has a topic of &quot;I am King of
 * Test&quot; then the response will be &quot;
 * <code>PircBotY #test :I Am King of Test</code>&quot; with a code of 332 to
 * signify that this is a topic. (This is just an example - note that listening
 * for {@link TopicEvent} is an easier way of finding the topic for a channel).
 * Check the IRC RFC for the full list of other command response codes.
 * <p>
 * PircBotY uses the class ReplyConstants, which contains constants that you may
 * find useful here.
 *
 * @see ReplyConstants
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerResponseEvent<T extends PircBotY> extends Event<T> {

    protected final int code;
    protected final String rawLine;
    protected final ImmutableList<String> parsedResponse;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param code The three-digit numerical code for the response.
     */
    public ServerResponseEvent(T bot, int code, @NonNull String rawLine, @NonNull ImmutableList<String> parsedResponse) {
        super(bot);
        this.code = code;
        this.rawLine = rawLine;
        this.parsedResponse = parsedResponse;
    }

    /**
     * Respond with a <i>raw line</i> to the server
     *
     * @param response The response to send
     */
    @Override
    public void respond(@Nullable String response) {
        getBot().sendRaw().rawLine(response);
    }
}

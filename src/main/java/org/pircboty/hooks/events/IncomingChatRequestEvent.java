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

import java.io.IOException;
import java.net.InetAddress;
import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.dcc.ReceiveChat;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.Listener;
import org.pircboty.hooks.types.GenericDCCEvent;

/**
 * This event will be dispatched whenever a DCC Chat request is received. This
 * means that a client has requested to chat to us directly rather than via the
 * IRC server. This is useful for sending many lines of text to and from the bot
 * without having to worry about flooding the server or any operators of the
 * server being able to "spy" on what is being said. By default there are no
 * {@link Listener} for this event, which means that all DCC CHAT requests will
 * be ignored by default.
 * <p>
 * If you wish to accept the connection, then you listen for this event and call
 * the {@link DccChat#accept()} method, which connects to the sender of the chat
 * request and allows lines to be sent to and from the bot.
 * <p>
 * Your bot must be able to connect directly to the user that sent the request.
 * <p>
 * Example:
 * <pre>
 *     DccChat chat = event.getChat();
 *     // Accept all chat, whoever it's from.
 *     chat.accept();
 *     chat.sendLine("Hello");
 *     String response = chat.readLine();
 *     chat.close();
 * </pre>
 *
 * @author
 * @see DccChat
 */
public class IncomingChatRequestEvent<T extends PircBotY> extends Event<T> implements GenericDCCEvent<T> {

    private final User user;
    private final InetAddress chatAddress;
    private final int chatPort;
    private final String chatToken;
    private final boolean passive;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param chat A DccChat object that represents the incoming chat request.
     */
    public IncomingChatRequestEvent(T bot, User user, InetAddress chatAddress, int chatPort, String chatToken, boolean passive) {
        super(bot);
        this.user = user;
        this.chatAddress = chatAddress;
        this.chatPort = chatPort;
        this.chatToken = chatToken;
        this.passive = passive;
    }

    public ReceiveChat accept() throws IOException {
        return getBot().getDccHandler().acceptChatRequest(this);
    }

    /**
     * Respond with a <i>private message</i> to the user that sent the request,
     * <b>not a message over dcc</b> since it might not of been accepted yet
     *
     * @param response The response to send
     */
    @Override
    public void respond(String response) {
        getUser().send().message(response);
    }

    @Override
    public User getUser() {
        return user;
    }

    public InetAddress getChatAddress() {
        return chatAddress;
    }

    public int getChatPort() {
        return chatPort;
    }

    public String getChatToken() {
        return chatToken;
    }

    @Override
    public boolean isPassive() {
        return passive;
    }
}

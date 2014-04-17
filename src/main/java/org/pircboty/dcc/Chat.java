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
package org.pircboty.dcc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import org.apache.commons.lang3.Validate;
import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.exception.DccException;

/**
 * Generic DCC chat handling class that represents an active dcc chat.
 *
 * @author
 */
public class Chat {

    private final User user;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final Socket socket;
    private boolean finished;

    protected Chat(User user, Socket socket, Charset encoding) throws IOException {
        Validate.notNull(user, "User cannot be null");
        Validate.notNull(socket, "Socket cannot be null");
        Validate.notNull(encoding, "Encoding cannot be null");
        this.user = user;
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));
    }

    /**
     * Reads the next line of text from the client at the other end of our DCC
     * Chat connection. This method blocks until something can be returned. If
     * the connection has closed, null is returned.
     *
     * @return The next line of text from the client. Returns null if the
     * connection has closed normally.
     *
     * @throws IOException If an I/O error occurs.
     */
    public String readLine() throws IOException {
        if (finished) {
            throw new DccException(DccException.Reason.ChatNotConnected, user, "Chat has already finished");
        }
        String line = bufferedReader.readLine();
        PircBotY.getLogger().info("<<<" + line);
        return line;
    }

    /**
     * Sends a line of text to the client at the other end of our DCC Chat
     * connection.
     *
     * @param line The line of text to be sent. This should not include linefeed
     * characters.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void sendLine(String line) throws IOException {
        Validate.notNull(line, "Line cannot be null");
        if (finished) {
            throw new DccException(DccException.Reason.ChatNotConnected, user, "Chat has already finished");
        }
        synchronized (bufferedWriter) {
            PircBotY.getLogger().info(">>>" + line);
            bufferedWriter.write(line + "\r\n");
            bufferedWriter.flush();
        }
    }

    /**
     * Closes the DCC Chat connection.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void close() throws IOException {
        if (finished) {
            throw new DccException(DccException.Reason.ChatNotConnected, user, "Chat has already finished");
        }
        finished = true;
        socket.close();
    }

    public User getUser() {
        return user;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isFinished() {
        return finished;
    }
}

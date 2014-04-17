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

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import org.pircboty.Configuration;
import org.pircboty.PircBotY;
import org.pircboty.User;

/**
 * A general active DCC file transfer
 *
 * @author
 */
public abstract class FileTransfer {

    private final Configuration<PircBotY> configuration;
    private final Socket socket;
    private final User user;
    private final File file;
    private final long startPosition;
    private DccState state = DccState.INIT;
    private final Object stateLock = new Object();

    public FileTransfer(Configuration<PircBotY> configuration, Socket socket, User user, File file, long startPosition) {
        this.configuration = configuration;
        this.socket = socket;
        this.user = user;
        this.file = file;
        this.startPosition = startPosition;
    }

    /**
     * Transfer the file to the user
     *
     * @throws IOException If an error occurred during transfer
     */
    public void transfer() throws IOException {
        //Prevent being called multiple times
        if (state != DccState.INIT) {
            synchronized (stateLock) {
                if (state != DccState.INIT) {
                    throw new RuntimeException("Cannot receive file twice (Current state: " + state + ")");
                }
            }
        }
        state = DccState.RUNNING;
        transferFile();
        state = DccState.DONE;
    }

    protected abstract void transferFile() throws IOException;

    /**
     * Callback at end of read/write loop:
     * <p>
     * Receive: Socket read -> file write -> socket write (bytes transferred) ->
     * callback -> repeat
     * <p>
     * Send: File read -> socket write -> socket read (bytes transferred) ->
     * callback -> repeat
     */
    protected void onAfterSend() {
    }

    /**
     * Is the transfer finished?
     *
     * @return True if its finished
     */
    public boolean isFinished() {
        return state == DccState.DONE;
    }

    public Configuration<PircBotY> getConfiguration() {
        return configuration;
    }

    public Socket getSocket() {
        return socket;
    }

    public User getUser() {
        return user;
    }

    public File getFile() {
        return file;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public abstract long getBytesTransfered();

    public DccState getState() {
        return state;
    }

    public Object getStateLock() {
        return stateLock;
    }
}

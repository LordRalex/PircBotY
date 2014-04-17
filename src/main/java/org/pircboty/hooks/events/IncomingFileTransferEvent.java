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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import org.pircboty.PircBotY;
import org.pircboty.User;
import org.pircboty.dcc.ReceiveFileTransfer;
import org.pircboty.hooks.Event;
import org.pircboty.hooks.Listener;
import org.pircboty.hooks.types.GenericDCCEvent;

/**
 * This event is dispatched whenever a DCC SEND request is sent to the PircBotY.
 * This means that a client has requested to send a file to us. By default there
 * are no {@link Listener listeners} for this event, which means that all DCC
 * SEND requests will be ignored by default. If you wish to receive the file,
 * then you must listen for this event and call the receive method on the
 * DccFileTransfer object, which connects to the sender and downloads the file.
 * <p>
 * Example:
 * <pre>
 *     DccFileTransfer transfer = event.getTransfer();
 *     // Use the suggested file name.
 *     File file = transfer.getFile();
 *     // Receive the transfer and save it to the file, allowing resuming.
 *     transfer.receive(file, true);
 * </pre>
 * <p>
 * <b>Warning:</b> Receiving an incoming file transfer will cause a file to be
 * written to disk. Please ensure that you make adequate security checks so that
 * this file does not overwrite anything important!
 * <p>
 * If you allow resuming and the file already partly exists, it will be appended
 * to instead of overwritten. If resuming is not enabled, the file will be
 * overwritten if it already exists.
 * <p>
 * You can throttle the speed of the transfer by calling
 * {@link DccFileTransfer#setPacketDelay(long) } method on the DccFileTransfer
 * object, either before you receive the file or at any moment during the
 * transfer.
 *
 * @author
 * @see DccFileTransfer
 */
public class IncomingFileTransferEvent<T extends PircBotY> extends Event<T> implements GenericDCCEvent<T> {

    private final User user;
    private final String rawFilename;
    private final String safeFilename;
    private final InetAddress address;
    private final int port;
    private final long filesize;
    private final String transferToken;
    private final boolean passive;

    /**
     * Default constructor to setup object. Timestamp is automatically set to
     * current time as reported by {@link System#currentTimeMillis() }
     *
     * @param transfer The DcccFileTransfer that you may accept.
     */
    public IncomingFileTransferEvent(T bot, User user, String rawFilename, String safeFilename,
            InetAddress address, int port, long filesize, String transferToken, boolean passive) {
        super(bot);
        this.user = user;
        this.rawFilename = rawFilename;
        this.safeFilename = safeFilename;
        this.address = address;
        this.port = port;
        this.filesize = filesize;
        this.transferToken = transferToken;
        this.passive = passive;
    }

    public ReceiveFileTransfer accept(File destination) throws IOException {
        return user.getBot().getDccHandler().acceptFileTransfer(this, destination);
    }

    public ReceiveFileTransfer acceptResume(File destination, long startPosition) throws IOException, InterruptedException {
        return user.getBot().getDccHandler().acceptFileTransferResume(this, destination, startPosition);
    }

    /**
     * Respond with a <i>private message</i> to the user that sent the request
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

    public String getRawFilename() {
        return rawFilename;
    }

    public String getSafeFilename() {
        return safeFilename;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public long getFilesize() {
        return filesize;
    }

    public String getTransferToken() {
        return transferToken;
    }

    @Override
    public boolean isPassive() {
        return passive;
    }
}

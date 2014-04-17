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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import org.pircboty.Configuration;
import org.pircboty.PircBotY;
import org.pircboty.User;

/**
 * A DCC File Transfer initiated by the bot
 *
 * @author
 */
public class SendFileTransfer extends FileTransfer {

    private long bytesTransfered;

    public SendFileTransfer(Configuration<PircBotY> configuration, Socket socket, User user, File file, long startPosition) {
        super(configuration, socket, user, file, startPosition);
    }

    @Override
    protected void transferFile() throws IOException {
        BufferedOutputStream socketOutput = null;
        BufferedInputStream socketInput = null;
        BufferedInputStream fileInput = null;
        try {
            socketOutput = new BufferedOutputStream(getSocket().getOutputStream());
            socketInput = new BufferedInputStream(getSocket().getInputStream());
            fileInput = new BufferedInputStream(new FileInputStream(getFile()));
            // Check for resuming.
            if (getStartPosition() > 0) {
                long bytesSkipped = 0;
                while (bytesSkipped < getStartPosition()) {
                    bytesSkipped += fileInput.skip(getStartPosition() - bytesSkipped);
                }
            }
            byte[] outBuffer = new byte[getConfiguration().getDccTransferBufferSize()];
            byte[] inBuffer = new byte[4];
            int bytesRead;
            while ((bytesRead = fileInput.read(outBuffer, 0, outBuffer.length)) != -1) {
                socketOutput.write(outBuffer, 0, bytesRead);
                socketOutput.flush();
                socketInput.read(inBuffer, 0, inBuffer.length);
                bytesTransfered += bytesRead;
                onAfterSend();
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (socketInput != null) {
                try {
                    socketInput.close();
                } catch (IOException e) {
                }
            }
            if (socketOutput != null) {
                try {
                    socketOutput.close();
                } catch (IOException e) {
                }
            }
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public long getBytesTransfered() {
        return bytesTransfered;
    }
}

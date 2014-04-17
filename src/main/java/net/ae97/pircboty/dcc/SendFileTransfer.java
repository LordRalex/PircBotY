package net.ae97.pircboty.dcc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import net.ae97.pircboty.Configuration;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;

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

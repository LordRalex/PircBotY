package net.ae97.pircboty.dcc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import net.ae97.pircboty.Configuration;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;

public class ReceiveFileTransfer extends FileTransfer {

    private long bytesTransfered;

    public ReceiveFileTransfer(Configuration<PircBotY> configuration, Socket socket, User user, File file, long startPosition) {
        super(configuration, socket, user, file, startPosition);
    }

    @Override
    protected void transferFile() throws IOException {
        BufferedInputStream socketInput = null;
        OutputStream socketOutput = null;
        RandomAccessFile fileOutput = null;
        try {
            socketInput = new BufferedInputStream(getSocket().getInputStream());
            socketOutput = getSocket().getOutputStream();
            fileOutput = new RandomAccessFile(getFile().getCanonicalPath(), "rw");
            fileOutput.seek(getStartPosition());
            byte[] inBuffer = new byte[getConfiguration().getDccTransferBufferSize()];
            byte[] outBuffer = new byte[4];
            int bytesRead;
            while ((bytesRead = socketInput.read(inBuffer, 0, inBuffer.length)) != -1) {
                fileOutput.write(inBuffer, 0, bytesRead);
                bytesTransfered += bytesRead;
                outBuffer[0] = (byte) ((bytesTransfered >> 24) & 0xff);
                outBuffer[1] = (byte) ((bytesTransfered >> 16) & 0xff);
                outBuffer[2] = (byte) ((bytesTransfered >> 8) & 0xff);
                outBuffer[3] = (byte) (bytesTransfered & 0xff);
                socketOutput.write(outBuffer);
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
            if (fileOutput != null) {
                try {
                    fileOutput.close();
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

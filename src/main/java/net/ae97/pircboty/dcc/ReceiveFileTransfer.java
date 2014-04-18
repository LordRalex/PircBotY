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
        try (BufferedInputStream socketInput = new BufferedInputStream(getSocket().getInputStream())) {
            try (OutputStream socketOutput = getSocket().getOutputStream()) {
                try (RandomAccessFile fileOutput = new RandomAccessFile(getFile().getCanonicalPath(), "rw")) {
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
                }
            }
        }
    }

    @Override
    public long getBytesTransfered() {
        return bytesTransfered;
    }
}

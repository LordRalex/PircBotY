package net.ae97.pircboty.dcc;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import net.ae97.pircboty.Configuration;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;

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

    public void transfer() throws IOException {
        if (state != DccState.INIT) {
            synchronized (stateLock) {
                if (state != DccState.INIT) {
                    throw new IOException("Cannot receive file twice (Current state: " + state + ")");
                }
            }
        }
        state = DccState.RUNNING;
        transferFile();
        state = DccState.DONE;
    }

    protected abstract void transferFile() throws IOException;

    protected void onAfterSend() {
    }

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

package net.ae97.pircboty.hooks.events;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.dcc.ReceiveFileTransfer;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.types.GenericDCCEvent;

public class IncomingFileTransferEvent extends Event implements GenericDCCEvent {

    private final User user;
    private final String rawFilename;
    private final String safeFilename;
    private final InetAddress address;
    private final int port;
    private final long filesize;
    private final String transferToken;
    private final boolean passive;

    public IncomingFileTransferEvent(PircBotY bot, User user, String rawFilename, String safeFilename,
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

package net.ae97.pircboty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

public class IdentServer extends Thread implements AutoCloseable {

    private final Charset encoding;
    private final ServerSocket serverSocket;
    private final List<IdentEntry> identEntries = new LinkedList<>();
    private final String ip;
    private final int port;
    private final Logger logger;

    public IdentServer(Charset encoding, String ip, int port) throws IOException {
        super();
        this.ip = ip;
        this.port = port;
        this.encoding = encoding;
        this.serverSocket = new ServerSocket();
        this.setName("IdentServer");
        logger = new PrefixLogger("IdentServer");
        logger.setParent(PircBotY.getLogger());
        logger.info("Created ident server on " + port);
    }

    public Charset getEncoding() {
        return encoding;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<IdentEntry> getIdentEntries() {
        return identEntries;
    }

    @Override
    public void start() {
        logger.info("Starting ident server");
        try {
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(ip), port));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not create server socket", ex);
            return;
        }
        super.start();
        logger.info("Ident server started");
    }

    @Override
    public void run() {
        try (IdentServer server = this) {
            logger.info("IdentServer running on port " + port);
            while (!isInterrupted()) {
                handleNextConnection();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception encountered when running IdentServer", e);
        }
    }

    private void handleNextConnection() {
        try (Socket socket = serverSocket.accept()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));
            InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
            String line = reader.readLine();
            if (StringUtils.isBlank(line)) {
                logger.log(Level.SEVERE, "Ignoring connection from " + remoteAddress + ", received blank line");
                socket.close();
                return;
            }
            String[] parsedLine = StringUtils.split(line, ", ");
            if (parsedLine.length != 2) {
                logger.log(Level.SEVERE, "Ignoring connection from " + remoteAddress + ", recieved unknown line: " + line);
                socket.close();
                return;
            }
            int localPort = Utils.tryParseInt(parsedLine[0], -1);
            int remotePort = Utils.tryParseInt(parsedLine[1], -1);
            if (localPort == -1 || remotePort == -1) {
                logger.log(Level.SEVERE, "Ignoring connection from " + remoteAddress + ", recieved unparsable line: " + line);
                socket.close();
                return;
            }
            logger.log(Level.INFO, "Received ident request from " + remoteAddress + ": " + line);
            IdentEntry identEntry = null;
            synchronized (identEntries) {
                for (IdentEntry curIdentEntry : identEntries) {
                    if (curIdentEntry.getRemoteAddress().equals(remoteAddress.getAddress())
                            && curIdentEntry.getRemotePort() == remotePort
                            && curIdentEntry.getLocalPort() == localPort) {
                        identEntry = curIdentEntry;
                        break;
                    }
                }
            }
            if (identEntry == null) {
                String response = localPort + ", " + remotePort + " ERROR : NO-USER";
                logger.log(Level.SEVERE, "Unknown ident " + line + " from " + remoteAddress + ", responding with: " + response);
                writer.write(response + "\r\n");
                writer.flush();
                socket.close();
                return;
            }
            String response = line + " : USERID : UNIX : " + identEntry.getLogin();
            logger.log(Level.INFO, "Responded to ident request from " + remoteAddress + " with: " + response);
            writer.write(response + "\r\n");
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception encountered when running IdentServer", e);
        }
    }

    protected void addIdentEntry(InetAddress remoteAddress, int remotePort, int localPort, String login) {
        synchronized (identEntries) {
            logger.log(Level.INFO, "Adding entry: " + remoteAddress + ":" + remotePort + ", " + localPort + " -> " + login);
            identEntries.add(new IdentEntry(remoteAddress, remotePort, localPort, login));
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        identEntries.clear();
        interrupt();
        logger.info("Closed ident server on port " + port);
    }
}

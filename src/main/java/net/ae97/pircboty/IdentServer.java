package net.ae97.pircboty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;

public class IdentServer extends Thread implements Closeable {

    private static final int PORT = 113;
    private static IdentServer server;
    private final Charset encoding;
    private final ServerSocket serverSocket;
    private final List<IdentEntry> identEntries = new ArrayList<IdentEntry>();

    public static int getPORT() {
        return PORT;
    }

    public static IdentServer getServer() {
        return server;
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

    public static void startServer() {
        startServer(Charset.defaultCharset());
    }

    public static void startServer(Charset encoding) {
        if (server != null) {
            throw new RuntimeException("Already created an IdentServer instance");
        }
        server = new IdentServer(encoding);
        server.start();
    }

    public static void stopServer() throws IOException {
        if (server == null) {
            throw new RuntimeException("Never created an IdentServer");
        }
        server.close();
        server = null;
    }

    protected IdentServer(Charset encoding) {
        super();
        try {
            this.encoding = encoding;
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException("Could not create server socket for IdentServer on port " + PORT, e);
        }
    }

    @Override
    public void start() {
        this.setName("IdentServer");
        super.start();
    }

    @Override
    public void run() {
        try {
            PircBotY.getLogger().info("IdentServer running on port " + PORT);
            while (!isInterrupted()) {
                handleNextConnection();
            }
        } catch (IOException e) {
            PircBotY.getLogger().log(Level.SEVERE, "Exception encountered when running IdentServer", e);
        } finally {
            try {
                close();
            } catch (IOException e) {
                PircBotY.getLogger().log(Level.SEVERE, "Cannot close IdentServer socket", e);
            }
        }
    }

    private void handleNextConnection() throws IOException {
        Socket socket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), encoding));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), encoding));
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        String line = reader.readLine();
        if (StringUtils.isBlank(line)) {
            PircBotY.getLogger().log(Level.SEVERE, "Ignoring connection from " + remoteAddress + ", received blank line");
            socket.close();
            return;
        }
        String[] parsedLine = StringUtils.split(line, ", ");
        if (parsedLine.length != 2) {
            PircBotY.getLogger().log(Level.SEVERE, "Ignoring connection from " + remoteAddress + ", recieved unknown line: " + line);
            socket.close();
            return;
        }
        int localPort = Utils.tryParseInt(parsedLine[0], -1);
        int remotePort = Utils.tryParseInt(parsedLine[1], -1);
        if (localPort == -1 || remotePort == -1) {
            PircBotY.getLogger().log(Level.SEVERE, "Ignoring connection from " + remoteAddress + ", recieved unparsable line: " + line);
            socket.close();
            return;
        }
        PircBotY.getLogger().log(Level.FINE, "Received ident request from " + remoteAddress + ": " + line);
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
            PircBotY.getLogger().log(Level.SEVERE, "Unknown ident " + line + " from " + remoteAddress + ", responding with: " + response);
            writer.write(line + "\r\n");
            writer.flush();
            socket.close();
            return;
        }
        String response = line + " : USERID : UNIX : " + identEntry.getLogin();
        PircBotY.getLogger().log(Level.FINE, "Responded to ident request from " + remoteAddress + " with: " + response);
        writer.write(line + "\r\n");
        writer.flush();
        socket.close();
    }

    protected void addIdentEntry(InetAddress remoteAddress, int remotePort, int localPort, String login) {
        synchronized (identEntries) {
            identEntries.add(new IdentEntry(remoteAddress, remotePort, localPort, login));
        }
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        identEntries.clear();
        PircBotY.getLogger().info("Closed ident server on port " + PORT);
    }
}

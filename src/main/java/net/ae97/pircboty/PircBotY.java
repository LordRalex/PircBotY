package net.ae97.pircboty;

import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ae97.pircboty.api.events.DisconnectEvent;
import net.ae97.pircboty.api.events.SocketConnectEvent;
import net.ae97.pircboty.dcc.DccHandler;
import net.ae97.pircboty.exception.IrcException;
import net.ae97.pircboty.output.OutputCAP;
import net.ae97.pircboty.output.OutputDCC;
import net.ae97.pircboty.output.OutputIRC;
import net.ae97.pircboty.output.OutputRaw;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;
import net.ae97.pokebot.logger.PrefixLogger;
import org.apache.commons.lang3.StringUtils;

public class PircBotY implements Comparable<PircBotY> {

    public static final String VERSION = "3.0.0";
    private static final AtomicInteger BOT_COUNT = new AtomicInteger();
    private final int botId;
    private final Configuration<PircBotY> configuration;
    private final List<String> enabledCapabilities = new LinkedList<>();
    private InputParser inputParser;
    private UserChannelDao<PircBotY, User, Channel> userChannelDao;
    private DccHandler dccHandler;
    private ServerInfo serverInfo;
    private Socket socket;
    private BufferedReader inputReader;
    private OutputStreamWriter outputWriter;
    private OutputRaw outputRaw;
    private OutputIRC outputIRC;
    private OutputCAP outputCAP;
    private OutputDCC outputDCC;
    private String nick = "";
    private boolean loggedIn = false;
    private Thread shutdownHook;
    private boolean reconnectStopped = false;
    private ImmutableMap<String, String> reconnectChannels;
    private State state = State.INIT;
    private Exception disconnectException;
    private InputProcessor inputProcessor;
    private static final Logger logger = new PrefixLogger("PircBotY");
    private IdentServer identServer;

    public static Logger getLogger() {
        return logger;
    }

    public PircBotY(Configuration<PircBotY> configuration) {
        botId = BOT_COUNT.getAndIncrement();
        this.configuration = configuration;
    }

    public void startBot() throws IOException, IrcException {
        reconnectStopped = false;
        connect();
    }

    public void stopBotReconnect() {
        reconnectStopped = true;
    }

    protected void connect() throws IOException, IrcException {
        if (isConnected()) {
            throw new IrcException(IrcException.Reason.AlreadyConnected, "Must disconnect from server before connecting again");
        }
        if (getState() == State.CONNECTED) {
            throw new RuntimeException("Bot is not connected but state is State.CONNECTED. This shouldn't happen");
        }
        this.userChannelDao = configuration.getBotFactory().createUserChannelDao(this);
        this.serverInfo = configuration.getBotFactory().createServerInfo(this);
        this.outputRaw = configuration.getBotFactory().createOutputRaw(this);
        this.outputIRC = configuration.getBotFactory().createOutputIRC(this);
        this.outputCAP = configuration.getBotFactory().createOutputCAP(this);
        this.outputDCC = configuration.getBotFactory().createOutputDCC(this);
        this.dccHandler = configuration.getBotFactory().createDccHandler(this);
        this.inputParser = configuration.getBotFactory().createInputParser(this);
        enabledCapabilities.clear();
        getLogger().info("Settings set, starting connection");
        if (configuration.isIdentServerEnabled()) {
            identServer = new IdentServer(configuration.getEncoding(), configuration.getIdentServerIP(), configuration.getIdentServerPort());
            identServer.start();
        }
        getLogger().info("Starting IRC connection attempt");
        for (InetAddress curAddress : InetAddress.getAllByName(configuration.getServerHostname())) {
            PircBotY.getLogger().log(Level.INFO, "Trying address " + curAddress);
            try {
                socket = configuration.getSocketFactory().createSocket(curAddress, configuration.getServerPort(), configuration.getLocalAddress(), 0);
                break;
            } catch (IOException e) {
                PircBotY.getLogger().log(Level.INFO, "Unable to connect to " + configuration.getServerHostname() + " using the IP address " + curAddress.getHostAddress() + ", trying to check another address.", e);
            }
        }
        if (socket == null || (socket != null && !socket.isConnected())) {
            throw new IOException("Unable to connect to the IRC network " + configuration.getServerHostname());
        }
        state = State.CONNECTED;
        socket.setSoTimeout(configuration.getSocketTimeout());
        PircBotY.getLogger().info("Connected to server.");
        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), configuration.getEncoding()));
        outputWriter = new OutputStreamWriter(socket.getOutputStream(), configuration.getEncoding());
        configuration.getListenerManager().dispatchEvent(new SocketConnectEvent(this));
        sendRaw().rawLine("CAP REQ account-notify");
        sendRaw().rawLine("CAP END");
        if (configuration.isIdentServerEnabled()) {
            identServer.addIdentEntry(socket.getInetAddress(), socket.getPort(), socket.getLocalPort(), configuration.getLogin());
        }
        if (configuration.isCapEnabled()) {
            sendCAP().requestSupported();
        }
        if (configuration.isWebIrcEnabled()) {
            sendRaw().rawLineNow("WEBIRC " + configuration.getWebIrcPassword()
                    + " " + configuration.getWebIrcUsername()
                    + " " + configuration.getWebIrcHostname()
                    + " " + configuration.getWebIrcAddress().getHostAddress());
        }
        if (StringUtils.isNotBlank(configuration.getServerPassword())) {
            sendRaw().rawLineNow("PASS " + configuration.getServerPassword());
        }
        sendRaw().rawLineNow("NICK " + configuration.getName());
        sendRaw().rawLineNow("USER " + configuration.getLogin() + " 8 * :" + configuration.getRealName());
        inputProcessor = new InputProcessor();
        inputProcessor.start();
    }

    protected void sendRawLineToServer(String line) {
        if (line.length() > configuration.getMaxLineLength() - 2) {
            line = line.substring(0, configuration.getMaxLineLength() - 2);
        }
        try {
            outputWriter.write(line + "\r\n");
            outputWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Exception encountered when writing to socket", e);
        }
    }

    protected void loggedIn(String nick) {
        this.loggedIn = true;
        setNick(nick);
        if (configuration.isShutdownHookEnabled()) {
            Runtime.getRuntime().addShutdownHook(shutdownHook = new PircBotY.BotShutdownHook(this));
        }
    }

    public OutputRaw sendRaw() {
        return outputRaw;
    }

    public OutputIRC sendIRC() {
        return outputIRC;
    }

    public OutputCAP sendCAP() {
        return outputCAP;
    }

    public OutputDCC sendDCC() {
        return outputDCC;
    }

    public int getBotId() {
        return botId;
    }

    public Configuration<PircBotY> getConfiguration() {
        return configuration;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public UserChannelDao<PircBotY, User, Channel> getUserChannelDao() {
        return userChannelDao;
    }

    public List<String> getEnabledCapabilities() {
        return enabledCapabilities;
    }

    public OutputRaw getOutputRaw() {
        return outputRaw;
    }

    public OutputIRC getOutputIRC() {
        return outputIRC;
    }

    public OutputCAP getOutputCAP() {
        return outputCAP;
    }

    public Thread getShutdownHook() {
        return shutdownHook;
    }

    public boolean isReconnectStopped() {
        return reconnectStopped;
    }

    public ImmutableMap<String, String> getReconnectChannels() {
        return reconnectChannels;
    }

    public Exception getDisconnectException() {
        return disconnectException;
    }

    public DccHandler getDccHandler() {
        return dccHandler;
    }

    public InputParser getInputParser() {
        return inputParser;
    }

    protected void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    @Override
    public String toString() {
        return "Version{" + configuration.getVersion() + "}"
                + " Connected{" + isConnected() + "}"
                + " Server{" + configuration.getServerHostname() + "}"
                + " Port{" + configuration.getServerPort() + "}"
                + " Password{" + configuration.getServerPassword() + "}";
    }

    public User getUserBot() {
        return userChannelDao.getUser(getNick());
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    protected ImmutableMap<String, String> reconnectChannels() {
        ImmutableMap<String, String> reconnectChannelsLocal = reconnectChannels;
        reconnectChannels = null;
        return reconnectChannelsLocal;
    }

    public void shutdown() {
        shutdown(false);
    }

    public void shutdown(boolean noReconnect) {
        UserChannelDao<PircBotY, UserSnapshot, ChannelSnapshot> daoSnapshot;
        if (state == State.DISCONNECTED) {
            throw new RuntimeException("Cannot call shutdown twice");
        }
        state = State.DISCONNECTED;
        try {
            socket.close();
        } catch (IOException e) {
            PircBotY.getLogger().log(Level.SEVERE, "Can't close socket", e);
        }
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                PircBotY.getLogger().log(Level.SEVERE, "Cannot close socket", e);
            }
        }
        ImmutableMap.Builder<String, String> reconnectChannelsBuilder = new ImmutableMap.Builder<>();
        userChannelDao.getAllChannels().stream().forEach((curChannel) -> {
            String key = (curChannel.getChannelKey() == null) ? "" : curChannel.getChannelKey();
            reconnectChannelsBuilder.put(curChannel.getName(), key);
        });
        reconnectChannels = reconnectChannelsBuilder.build();
        loggedIn = false;
        daoSnapshot = userChannelDao.createSnapshot();
        userChannelDao.close();
        inputParser.close();
        dccHandler.close();
        configuration.getListenerManager().dispatchEvent(new DisconnectEvent(this, daoSnapshot, disconnectException));
        disconnectException = null;
        PircBotY.getLogger().log(Level.FINE, "Disconnected.");
        configuration.getListenerManager().shutdown(this);
    }

    @Override
    public int compareTo(PircBotY other) {
        return getBotId() - other.getBotId();
    }

    public State getState() {
        return state;
    }

    protected class InputProcessor extends Thread {

        @Override
        public void run() {
            while (socket != null && socket.isConnected()) {
                String line;
                try {
                    line = inputReader.readLine();
                } catch (InterruptedIOException iioe) {
                    sendRaw().rawLine("PING " + (System.currentTimeMillis() / 1000));
                    continue;
                } catch (IOException e) {
                    if (e instanceof SocketException && PircBotY.this.getState() == PircBotY.State.DISCONNECTED) {
                        PircBotY.getLogger().info("Shutdown has been called, closing InputParser");
                        return;
                    } else {
                        disconnectException = e;
                        PircBotY.getLogger().log(Level.SEVERE, "Exception encountered when reading next line from server", e);
                        line = null;
                    }
                }
                if (line == null) {
                    break;
                }
                try {
                    inputParser.handleLine(line);
                } catch (IOException | IrcException e) {
                    PircBotY.getLogger().log(Level.SEVERE, "Exception encountered when parsing line", e);
                }
                if (interrupted()) {
                    return;
                }
            }
            shutdown();
            if (configuration.isAutoReconnect() && !reconnectStopped) {
                try {
                    connect();
                } catch (IOException | IrcException ex) {
                    PircBotY.getLogger().log(Level.SEVERE, "Exception encountered while reconnecting", ex);
                }
            }
        }
    }

    protected static class BotShutdownHook extends Thread {

        protected final WeakReference<PircBotY> thisBotRef;

        public BotShutdownHook(PircBotY bot) {
            this.thisBotRef = new WeakReference<>(bot);
            setName("bot" + BOT_COUNT + "-shutdownhook");
        }

        @Override
        public void run() {
            PircBotY thisBot = thisBotRef.get();
            if (thisBot != null && thisBot.getState() != PircBotY.State.DISCONNECTED) {
                try {
                    thisBot.stopBotReconnect();
                    thisBot.sendIRC().quitServer();
                } finally {
                    if (thisBot.getState() != PircBotY.State.DISCONNECTED) {
                        thisBot.shutdown(true);
                    }
                }
            }
        }
    }

    public static enum State {

        INIT,
        CONNECTED,
        DISCONNECTED
    }
}

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
package org.pircboty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.net.SocketFactory;
import org.apache.commons.lang3.Validate;
import org.pircboty.cap.CapHandler;
import org.pircboty.cap.EnableCapHandler;
import org.pircboty.dcc.DccHandler;
import org.pircboty.dcc.ReceiveChat;
import org.pircboty.dcc.ReceiveFileTransfer;
import org.pircboty.dcc.SendChat;
import org.pircboty.dcc.SendFileTransfer;
import org.pircboty.exception.IrcException;
import org.pircboty.hooks.CoreHooks;
import org.pircboty.hooks.Listener;
import org.pircboty.hooks.managers.ListenerManager;
import org.pircboty.hooks.managers.ThreadedListenerManager;
import org.pircboty.output.OutputCAP;
import org.pircboty.output.OutputChannel;
import org.pircboty.output.OutputDCC;
import org.pircboty.output.OutputIRC;
import org.pircboty.output.OutputRaw;
import org.pircboty.output.OutputUser;

/**
 * Immutable configuration for PircBotY. Use {@link Configuration.Builder} to
 * create
 *
 * @author
 */
public class Configuration<B extends PircBotY> {

    //WebIRC
    private final boolean webIrcEnabled;
    private final String webIrcUsername;
    private final String webIrcHostname;
    private final InetAddress webIrcAddress;
    private final String webIrcPassword;
    //Bot information
    private final String name;
    private final String login;
    private final String version;
    private final String finger;
    private final String realName;
    private final String channelPrefixes;
    //DCC
    private final boolean dccFilenameQuotes;
    private final ImmutableList<Integer> dccPorts;
    private final InetAddress dccLocalAddress;
    private final int dccAcceptTimeout;
    private final int dccResumeAcceptTimeout;
    private final int dccTransferBufferSize;
    private final boolean dccPassiveRequest;
    //Connect information
    private final String serverHostname;
    private final int serverPort;
    private final String serverPassword;
    private final SocketFactory socketFactory;
    private final InetAddress localAddress;
    private final Charset encoding;
    private final Locale locale;
    private final int socketTimeout;
    private final int maxLineLength;
    private final boolean autoSplitMessage;
    private final boolean autoNickChange;
    private final long messageDelay;
    private final boolean shutdownHookEnabled;
    private final ImmutableMap<String, String> autoJoinChannels;
    private final boolean identServerEnabled;
    private final String nickservPassword;
    private final boolean autoReconnect;
    //Bot classes
    private final ListenerManager<B> listenerManager;
    private final boolean capEnabled;
    private final ImmutableList<CapHandler> capHandlers;
    private final ImmutableSortedMap<Character, ChannelModeHandler> channelModeHandlers;
    private final BotFactory botFactory;

    /**
     * Use {@link Configuration.Builder#build() }.
     *
     * @param builder
     * @see Configuration.Builder#build()
     */
    private Configuration(Builder<B> builder) {
        //Check for basics
        if (builder.isWebIrcEnabled()) {
            Validate.notNull(builder.getWebIrcAddress(), "Must specify WEBIRC address if enabled");
            Validate.notBlank(builder.getWebIrcHostname(), "Must specify WEBIRC hostname if enabled");
            Validate.notBlank(builder.getWebIrcUsername(), "Must specify WEBIRC username if enabled");
            Validate.notBlank(builder.getWebIrcPassword(), "Must specify WEBIRC password if enabled");
        }
        Validate.notNull(builder.getListenerManager());
        Validate.notBlank(builder.getName(), "Must specify name");
        Validate.notBlank(builder.getLogin(), "Must specify login");
        Validate.notBlank(builder.getRealName(), "Must specify realName");
        Validate.notBlank(builder.getChannelPrefixes(), "Must specify channel prefixes");
        Validate.isTrue(builder.getDccAcceptTimeout() > 0, "dccAcceptTimeout must be positive");
        Validate.isTrue(builder.getDccResumeAcceptTimeout() > 0, "dccResumeAcceptTimeout must be positive");
        Validate.isTrue(builder.getDccTransferBufferSize() > 0, "dccTransferBufferSize must be positive");
        Validate.notBlank(builder.getServerHostname(), "Must specify server hostname");
        Validate.isTrue(builder.getServerPort() > 0 && builder.getServerPort() <= 65535, "Port must be between 1 and 65535");
        Validate.notNull(builder.getSocketFactory(), "Must specify socket factory");
        Validate.notNull(builder.getEncoding(), "Must specify encoding");
        Validate.notNull(builder.getLocale(), "Must specify locale");
        Validate.isTrue(builder.getSocketTimeout() >= 0, "Socket timeout must be positive");
        Validate.isTrue(builder.getMaxLineLength() > 0, "Max line length must be positive");
        Validate.isTrue(builder.getMessageDelay() >= 0, "Message delay must be positive");
        if (builder.getNickservPassword() != null) {
            Validate.notEmpty(builder.getNickservPassword(), "Nickserv password cannot be empty");
        }
        Validate.notNull(builder.getListenerManager(), "Must specify listener manager");
        Validate.notNull(builder.getBotFactory(), "Must specify bot factory");
        this.webIrcEnabled = builder.isWebIrcEnabled();
        this.webIrcUsername = builder.getWebIrcUsername();
        this.webIrcHostname = builder.getWebIrcHostname();
        this.webIrcAddress = builder.getWebIrcAddress();
        this.webIrcPassword = builder.getWebIrcPassword();
        this.name = builder.getName();
        this.login = builder.getLogin();
        this.version = builder.getVersion();
        this.finger = builder.getFinger();
        this.realName = builder.getRealName();
        this.channelPrefixes = builder.getChannelPrefixes();
        this.dccFilenameQuotes = builder.isDccFilenameQuotes();
        this.dccPorts = ImmutableList.copyOf(builder.getDccPorts());
        this.dccLocalAddress = builder.getDccLocalAddress();
        this.dccAcceptTimeout = builder.getDccAcceptTimeout();
        this.dccResumeAcceptTimeout = builder.getDccResumeAcceptTimeout();
        this.dccTransferBufferSize = builder.getDccTransferBufferSize();
        this.dccPassiveRequest = builder.isDccPassiveRequest();
        this.serverHostname = builder.getServerHostname();
        this.serverPort = builder.getServerPort();
        this.serverPassword = builder.getServerPassword();
        this.socketFactory = builder.getSocketFactory();
        this.localAddress = builder.getLocalAddress();
        this.encoding = builder.getEncoding();
        this.locale = builder.getLocale();
        this.socketTimeout = builder.getSocketTimeout();
        this.maxLineLength = builder.getMaxLineLength();
        this.autoSplitMessage = builder.isAutoSplitMessage();
        this.autoNickChange = builder.isAutoNickChange();
        this.messageDelay = builder.getMessageDelay();
        this.identServerEnabled = builder.isIdentServerEnabled();
        this.nickservPassword = builder.getNickservPassword();
        this.autoReconnect = builder.isAutoReconnect();
        this.listenerManager = builder.getListenerManager();
        this.autoJoinChannels = ImmutableMap.copyOf(builder.getAutoJoinChannels());
        this.capEnabled = builder.isCapEnabled();
        this.capHandlers = ImmutableList.copyOf(builder.getCapHandlers());
        ImmutableSortedMap.Builder<Character, ChannelModeHandler> channelModeHandlersBuilder = ImmutableSortedMap.naturalOrder();
        for (ChannelModeHandler curHandler : builder.getChannelModeHandlers()) {
            channelModeHandlersBuilder.put(curHandler.getMode(), curHandler);
        }
        this.channelModeHandlers = channelModeHandlersBuilder.build();
        this.shutdownHookEnabled = builder.isShutdownHookEnabled();
        this.botFactory = builder.getBotFactory();
    }

    public boolean isWebIrcEnabled() {
        return webIrcEnabled;
    }

    public String getWebIrcUsername() {
        return webIrcUsername;
    }

    public String getWebIrcHostname() {
        return webIrcHostname;
    }

    public InetAddress getWebIrcAddress() {
        return webIrcAddress;
    }

    public String getWebIrcPassword() {
        return webIrcPassword;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getVersion() {
        return version;
    }

    public String getFinger() {
        return finger;
    }

    public String getRealName() {
        return realName;
    }

    public String getChannelPrefixes() {
        return channelPrefixes;
    }

    public boolean isDccFilenameQuotes() {
        return dccFilenameQuotes;
    }

    public ImmutableList<Integer> getDccPorts() {
        return dccPorts;
    }

    public InetAddress getDccLocalAddress() {
        return dccLocalAddress;
    }

    public int getDccAcceptTimeout() {
        return dccAcceptTimeout;
    }

    public int getDccResumeAcceptTimeout() {
        return dccResumeAcceptTimeout;
    }

    public int getDccTransferBufferSize() {
        return dccTransferBufferSize;
    }

    public boolean isDccPassiveRequest() {
        return dccPassiveRequest;
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public Locale getLocale() {
        return locale;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getMaxLineLength() {
        return maxLineLength;
    }

    public boolean isAutoSplitMessage() {
        return autoSplitMessage;
    }

    public boolean isAutoNickChange() {
        return autoNickChange;
    }

    public long getMessageDelay() {
        return messageDelay;
    }

    public boolean isShutdownHookEnabled() {
        return shutdownHookEnabled;
    }

    public ImmutableMap<String, String> getAutoJoinChannels() {
        return autoJoinChannels;
    }

    public boolean isIdentServerEnabled() {
        return identServerEnabled;
    }

    public String getNickservPassword() {
        return nickservPassword;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public ListenerManager<B> getListenerManager() {
        return listenerManager;
    }

    public boolean isCapEnabled() {
        return capEnabled;
    }

    public ImmutableList<CapHandler> getCapHandlers() {
        return capHandlers;
    }

    public ImmutableSortedMap<Character, ChannelModeHandler> getChannelModeHandlers() {
        return channelModeHandlers;
    }

    public BotFactory getBotFactory() {
        return botFactory;
    }

    public static class Builder<B extends PircBotY> {
        //WebIRC
        /**
         * Enable or disable sending WEBIRC line on connect
         */
        private boolean webIrcEnabled = false;
        /**
         * Username of WEBIRC connection
         */
        private String webIrcUsername = null;
        /**
         * Hostname of WEBIRC connection
         */
        private String webIrcHostname = null;
        /**
         * IP address of WEBIRC connection
         */
        private InetAddress webIrcAddress = null;
        /**
         * Password of WEBIRC connection
         */
        private String webIrcPassword = null;
        //Bot information
        /**
         * The base name to be used for the IRC connection (nick!login@host)
         */
        private String name = "PircBotY";
        /**
         * The login to be used for the IRC connection (nick!login@host)
         */
        private String login = "PircBotY";
        /**
         * CTCP version response.
         */
        private String version = "PircBotY " + PircBotY.VERSION + ", a fork of PircBot, the Java IRC bot - PircBotY.googlecode.com";
        /**
         * CTCP finger response
         */
        private String finger = "You ought to be arrested for fingering a bot!";
        /**
         * The realName/fullname used for WHOIS info. Defaults to version
         */
        private String realName = version;
        /**
         * Allowed channel prefix characters. Defaults to <code>#&+!</code>
         */
        private String channelPrefixes = "#&+!";
        //DCC
        /**
         * If true sends filenames in quotes, otherwise uses underscores.
         * Defaults to false
         */
        private boolean dccFilenameQuotes = false;
        /**
         * Ports to allow DCC incoming connections. Recommended to set multiple
         * as DCC connections will be rejected if no free port can be found
         */
        private List<Integer> dccPorts = new ArrayList<Integer>();
        /**
         * The local address to bind DCC connections to. Defaults to {@link #getLocalAddress()
         * }
         */
        private InetAddress dccLocalAddress = null;
        /**
         * Timeout for user to accept a sent DCC request. Defaults to {@link #getSocketTimeout()
         * }
         */
        private int dccAcceptTimeout = -1;
        /**
         * Timeout for a user to accept a resumed DCC request. Defaults to {@link #getDccResumeAcceptTimeout()
         * }
         */
        private int dccResumeAcceptTimeout = -1;
        /**
         * Size of the DCC file transfer buffer. Defaults to 1024
         */
        private int dccTransferBufferSize = 1024;
        /**
         * Weather to send DCC Passive/reverse requests. Defaults to false
         */
        private boolean dccPassiveRequest = false;
        //Connect information
        /**
         * Hostname of the IRC server
         */
        private String serverHostname = null;
        /**
         * Port number of IRC server. Defaults to 6667
         */
        private int serverPort = 6667;
        /**
         * Password for IRC server
         */
        private String serverPassword = null;
        /**
         * Socket factory for connections. Defaults to {@link SocketFactory#getDefault()
         * }
         */
        private SocketFactory socketFactory = SocketFactory.getDefault();
        /**
         * Address to bind to when connecting to IRC server.
         */
        private InetAddress localAddress = null;
        /**
         * Charset encoding to use for connection. Defaults to
         * {@link Charset#defaultCharset()}
         */
        private Charset encoding = Charset.defaultCharset();
        /**
         * Locale to use for connection. Defaults to {@link Locale#getDefault()
         * }
         */
        private Locale locale = Locale.getDefault();
        /**
         * Timeout of IRC connection before sending PING. Defaults to 5 minutes
         */
        private int socketTimeout = 1000 * 60 * 5;
        /**
         * Maximum line length of IRC server. Defaults to 512
         */
        private int maxLineLength = 512;
        /**
         * Enable or disable automatic message splitting to fit
         * {@link #getMaxLineLength()}. Note that messages might be truncated by
         * the IRC server if not set. Defaults to true
         */
        private boolean autoSplitMessage = true;
        /**
         * Enable or disable automatic nick changing if a nick is in use by
         * adding a number to the end. If this is false and a nick is already in
         * use, a {@link IrcException} will be thrown. Defaults to false.
         */
        private boolean autoNickChange = false;
        /**
         * Millisecond delay between sending messages with {@link OutputRaw#rawLine(java.lang.String)
         * }. Defaults to 1000 milliseconds
         */
        private long messageDelay = 1000;
        /**
         * Enable or disable creating a JVM shutdown hook which will properly
         * QUIT the IRC server and shutdown the bot. Defaults to true
         */
        private boolean shutdownHookEnabled = true;
        /**
         * Map of channels and keys to automatically join upon connecting.
         */
        private final Map<String, String> autoJoinChannels = Maps.newHashMap();
        /**
         * Enable or disable use of an existing {@link IdentServer}. Note that
         * the IdentServer must be started separately or else an exception will
         * be thrown. Defaults to false
         *
         * @see IdentServer
         */
        private boolean identServerEnabled = false;
        /**
         * If set, password to authenticate against NICKSERV
         */
        private String nickservPassword;
        /**
         * Enable or disable automatic reconnecting. Note that you MUST call 
		 * {@link PircBotY#stopBotReconnect() } when you do not want the bot to
         * reconnect anymore! Defaults to false
         */
        private boolean autoReconnect = false;
        //Bot classes
        /**
         * The {@link ListenerManager} to use to handle events.
         */
        private ListenerManager<B> listenerManager = null;
        /**
         * Enable or disable CAP handling. Defaults to false
         */
        private boolean capEnabled = false;
        /**
         * Registered {@link CapHandler}'s.
         */
        private final List<CapHandler> capHandlers = new ArrayList<CapHandler>();
        private final List<ChannelModeHandler> channelModeHandlers = new ArrayList<ChannelModeHandler>();
        /**
         * The {@link BotFactory} to use
         */
        private BotFactory botFactory = new BotFactory();

        /**
         * Default constructor, adding a multi-prefix {@link EnableCapHandler}
         */
        public Builder() {
            capHandlers.add(new EnableCapHandler("multi-prefix", true));
            capHandlers.add(new EnableCapHandler("away-notify", true));
            channelModeHandlers.addAll(InputParser.getDefaultChannelModeHandlers());
        }

        /**
         * Copy values from an existing Configuration.
         *
         * @param configuration Configuration<B> to copy values from
         */
        public Builder(Configuration<B> configuration) {
            this.webIrcEnabled = configuration.isWebIrcEnabled();
            this.webIrcUsername = configuration.getWebIrcUsername();
            this.webIrcHostname = configuration.getWebIrcHostname();
            this.webIrcAddress = configuration.getWebIrcAddress();
            this.webIrcPassword = configuration.getWebIrcPassword();
            this.name = configuration.getName();
            this.login = configuration.getLogin();
            this.version = configuration.getVersion();
            this.finger = configuration.getFinger();
            this.realName = configuration.getRealName();
            this.channelPrefixes = configuration.getChannelPrefixes();
            this.dccFilenameQuotes = configuration.isDccFilenameQuotes();
            this.dccPorts.addAll(configuration.getDccPorts());
            this.dccLocalAddress = configuration.getDccLocalAddress();
            this.dccAcceptTimeout = configuration.getDccAcceptTimeout();
            this.dccResumeAcceptTimeout = configuration.getDccResumeAcceptTimeout();
            this.dccTransferBufferSize = configuration.getDccTransferBufferSize();
            this.dccPassiveRequest = configuration.isDccPassiveRequest();
            this.serverHostname = configuration.getServerHostname();
            this.serverPort = configuration.getServerPort();
            this.serverPassword = configuration.getServerPassword();
            this.socketFactory = configuration.getSocketFactory();
            this.localAddress = configuration.getLocalAddress();
            this.encoding = configuration.getEncoding();
            this.locale = configuration.getLocale();
            this.socketTimeout = configuration.getSocketTimeout();
            this.maxLineLength = configuration.getMaxLineLength();
            this.autoSplitMessage = configuration.isAutoSplitMessage();
            this.autoNickChange = configuration.isAutoNickChange();
            this.messageDelay = configuration.getMessageDelay();
            this.listenerManager = configuration.getListenerManager();
            this.nickservPassword = configuration.getNickservPassword();
            this.autoReconnect = configuration.isAutoReconnect();
            this.autoJoinChannels.putAll(configuration.getAutoJoinChannels());
            this.identServerEnabled = configuration.isIdentServerEnabled();
            this.capEnabled = configuration.isCapEnabled();
            this.capHandlers.addAll(configuration.getCapHandlers());
            this.channelModeHandlers.addAll(configuration.getChannelModeHandlers().values());
            this.shutdownHookEnabled = configuration.isShutdownHookEnabled();
            this.botFactory = configuration.getBotFactory();
        }

        /**
         * Copy values from another builder.
         *
         * @param otherBuilder<B>
         */
        public Builder(Builder<B> otherBuilder) {
            this.webIrcEnabled = otherBuilder.isWebIrcEnabled();
            this.webIrcUsername = otherBuilder.getWebIrcUsername();
            this.webIrcHostname = otherBuilder.getWebIrcHostname();
            this.webIrcAddress = otherBuilder.getWebIrcAddress();
            this.webIrcPassword = otherBuilder.getWebIrcPassword();
            this.name = otherBuilder.getName();
            this.login = otherBuilder.getLogin();
            this.version = otherBuilder.getVersion();
            this.finger = otherBuilder.getFinger();
            this.realName = otherBuilder.getRealName();
            this.channelPrefixes = otherBuilder.getChannelPrefixes();
            this.dccFilenameQuotes = otherBuilder.isDccFilenameQuotes();
            this.dccPorts.addAll(otherBuilder.getDccPorts());
            this.dccLocalAddress = otherBuilder.getDccLocalAddress();
            this.dccAcceptTimeout = otherBuilder.getDccAcceptTimeout();
            this.dccResumeAcceptTimeout = otherBuilder.getDccResumeAcceptTimeout();
            this.dccTransferBufferSize = otherBuilder.getDccTransferBufferSize();
            this.dccPassiveRequest = otherBuilder.isDccPassiveRequest();
            this.serverHostname = otherBuilder.getServerHostname();
            this.serverPort = otherBuilder.getServerPort();
            this.serverPassword = otherBuilder.getServerPassword();
            this.socketFactory = otherBuilder.getSocketFactory();
            this.localAddress = otherBuilder.getLocalAddress();
            this.encoding = otherBuilder.getEncoding();
            this.locale = otherBuilder.getLocale();
            this.socketTimeout = otherBuilder.getSocketTimeout();
            this.maxLineLength = otherBuilder.getMaxLineLength();
            this.autoSplitMessage = otherBuilder.isAutoSplitMessage();
            this.autoNickChange = otherBuilder.isAutoNickChange();
            this.messageDelay = otherBuilder.getMessageDelay();
            this.listenerManager = otherBuilder.getListenerManager();
            this.nickservPassword = otherBuilder.getNickservPassword();
            this.autoReconnect = otherBuilder.isAutoReconnect();
            this.autoJoinChannels.putAll(otherBuilder.getAutoJoinChannels());
            this.identServerEnabled = otherBuilder.isIdentServerEnabled();
            this.capEnabled = otherBuilder.isCapEnabled();
            this.capHandlers.addAll(otherBuilder.getCapHandlers());
            this.channelModeHandlers.addAll(otherBuilder.getChannelModeHandlers());
            this.shutdownHookEnabled = otherBuilder.isShutdownHookEnabled();
            this.botFactory = otherBuilder.getBotFactory();
        }

        public Builder<B> setWebIrcEnabled(boolean webIrcEnabled) {
            this.webIrcEnabled = webIrcEnabled;
            return this;
        }

        public Builder<B> setWebIrcUsername(String webIrcUsername) {
            this.webIrcUsername = webIrcUsername;
            return this;
        }

        public Builder<B> setWebIrcHostname(String webIrcHostname) {
            this.webIrcHostname = webIrcHostname;
            return this;
        }

        public Builder<B> setWebIrcAddress(InetAddress webIrcAddress) {
            this.webIrcAddress = webIrcAddress;
            return this;
        }

        public Builder<B> setWebIrcPassword(String webIrcPassword) {
            this.webIrcPassword = webIrcPassword;
            return this;
        }

        public Builder<B> setName(String name) {
            this.name = name;
            return this;
        }

        public Builder<B> setLogin(String login) {
            this.login = login;
            return this;
        }

        public Builder<B> setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder<B> setFinger(String finger) {
            this.finger = finger;
            return this;
        }

        public Builder<B> setRealName(String realName) {
            this.realName = realName;
            return this;
        }

        public Builder<B> setChannelPrefixes(String channelPrefixes) {
            this.channelPrefixes = channelPrefixes;
            return this;
        }

        public Builder<B> setDccFilenameQuotes(boolean dccFilenameQuotes) {
            this.dccFilenameQuotes = dccFilenameQuotes;
            return this;
        }

        public Builder<B> setDccPorts(List<Integer> dccPorts) {
            this.dccPorts = dccPorts;
            return this;
        }

        public Builder<B> setDccLocalAddress(InetAddress dccLocalAddress) {
            this.dccLocalAddress = dccLocalAddress;
            return this;
        }

        public Builder<B> setDccAcceptTimeout(int dccAcceptTimeout) {
            this.dccAcceptTimeout = dccAcceptTimeout;
            return this;
        }

        public Builder<B> setDccResumeAcceptTimeout(int dccResumeAcceptTimeout) {
            this.dccResumeAcceptTimeout = dccResumeAcceptTimeout;
            return this;
        }

        public Builder<B> setDccTransferBufferSize(int dccTransferBufferSize) {
            this.dccTransferBufferSize = dccTransferBufferSize;
            return this;
        }

        public Builder<B> setDccPassiveRequest(boolean dccPassiveRequest) {
            this.dccPassiveRequest = dccPassiveRequest;
            return this;
        }

        public Builder<B> setServerHostname(String serverHostname) {
            this.serverHostname = serverHostname;
            return this;
        }

        public Builder<B> setServerPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public Builder<B> setServerPassword(String serverPassword) {
            this.serverPassword = serverPassword;
            return this;
        }

        public Builder<B> setSocketFactory(SocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        public Builder<B> setLocalAddress(InetAddress localAddress) {
            this.localAddress = localAddress;
            return this;
        }

        public Builder<B> setEncoding(Charset encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder<B> setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder<B> setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder<B> setMaxLineLength(int maxLineLength) {
            this.maxLineLength = maxLineLength;
            return this;
        }

        public Builder<B> setAutoSplitMessage(boolean autoSplitMessage) {
            this.autoSplitMessage = autoSplitMessage;
            return this;
        }

        public Builder<B> setAutoNickChange(boolean autoNickChange) {
            this.autoNickChange = autoNickChange;
            return this;
        }

        public Builder<B> setMessageDelay(long messageDelay) {
            this.messageDelay = messageDelay;
            return this;
        }

        public Builder<B> setShutdownHookEnabled(boolean shutdownHookEnabled) {
            this.shutdownHookEnabled = shutdownHookEnabled;
            return this;
        }

        public Builder<B> setIdentServerEnabled(boolean identServerEnabled) {
            this.identServerEnabled = identServerEnabled;
            return this;
        }

        public Builder<B> setNickservPassword(String nickservPassword) {
            this.nickservPassword = nickservPassword;
            return this;
        }

        public Builder<B> setAutoReconnect(boolean autoReconnect) {
            this.autoReconnect = autoReconnect;
            return this;
        }

        public Builder<B> setCapEnabled(boolean capEnabled) {
            this.capEnabled = capEnabled;
            return this;
        }

        public Builder<B> setBotFactory(BotFactory botFactory) {
            this.botFactory = botFactory;
            return this;
        }

        /**
         * The local address to bind DCC connections to. Defaults to {@link #getLocalAddress()
         * }
         */
        public InetAddress getDccLocalAddress() {
            return (dccLocalAddress != null) ? dccLocalAddress : localAddress;
        }

        /**
         * Timeout for user to accept a sent DCC request. Defaults to {@link #getSocketTimeout()
         * }
         */
        public int getDccAcceptTimeout() {
            return (dccAcceptTimeout != -1) ? dccAcceptTimeout : socketTimeout;
        }

        /**
         * Timeout for a user to accept a resumed DCC request. Defaults to {@link #getDccResumeAcceptTimeout()
         * }
         */
        public int getDccResumeAcceptTimeout() {
            return (dccResumeAcceptTimeout != -1) ? dccResumeAcceptTimeout : getDccAcceptTimeout();
        }

        /**
         * Utility method for
         * <code>{@link #getCapHandlers()}.add(handler)</code>
         *
         * @param handler
         * @return
         */
        public Builder<B> addCapHandler(CapHandler handler) {
            getCapHandlers().add(handler);
            return this;
        }

        public boolean isWebIrcEnabled() {
            return webIrcEnabled;
        }

        public String getWebIrcUsername() {
            return webIrcUsername;
        }

        public String getWebIrcHostname() {
            return webIrcHostname;
        }

        public InetAddress getWebIrcAddress() {
            return webIrcAddress;
        }

        public String getWebIrcPassword() {
            return webIrcPassword;
        }

        public String getName() {
            return name;
        }

        public String getLogin() {
            return login;
        }

        public String getVersion() {
            return version;
        }

        public String getFinger() {
            return finger;
        }

        public String getRealName() {
            return realName;
        }

        public String getChannelPrefixes() {
            return channelPrefixes;
        }

        public boolean isDccFilenameQuotes() {
            return dccFilenameQuotes;
        }

        public List<Integer> getDccPorts() {
            return dccPorts;
        }

        public int getDccTransferBufferSize() {
            return dccTransferBufferSize;
        }

        public boolean isDccPassiveRequest() {
            return dccPassiveRequest;
        }

        public String getServerHostname() {
            return serverHostname;
        }

        public int getServerPort() {
            return serverPort;
        }

        public String getServerPassword() {
            return serverPassword;
        }

        public SocketFactory getSocketFactory() {
            return socketFactory;
        }

        public InetAddress getLocalAddress() {
            return localAddress;
        }

        public Charset getEncoding() {
            return encoding;
        }

        public Locale getLocale() {
            return locale;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public int getMaxLineLength() {
            return maxLineLength;
        }

        public boolean isAutoSplitMessage() {
            return autoSplitMessage;
        }

        public boolean isAutoNickChange() {
            return autoNickChange;
        }

        public long getMessageDelay() {
            return messageDelay;
        }

        public boolean isShutdownHookEnabled() {
            return shutdownHookEnabled;
        }

        public Map<String, String> getAutoJoinChannels() {
            return autoJoinChannels;
        }

        public boolean isIdentServerEnabled() {
            return identServerEnabled;
        }

        public String getNickservPassword() {
            return nickservPassword;
        }

        public boolean isAutoReconnect() {
            return autoReconnect;
        }

        public boolean isCapEnabled() {
            return capEnabled;
        }

        public List<CapHandler> getCapHandlers() {
            return capHandlers;
        }

        public List<ChannelModeHandler> getChannelModeHandlers() {
            return channelModeHandlers;
        }

        public BotFactory getBotFactory() {
            return botFactory;
        }

        /**
         * Utility method for
         * <code>{@link #getListenerManager().add(listener)</code>
         *
         * @param listener
         * @return
         */
        public Builder<B> addListener(Listener<B> listener) {
            getListenerManager().addListener(listener);
            return this;
        }

        /**
         * Utility method for
         * <code>{@link #getAutoJoinChannels().put(channel, "")</code>
         *
         * @param channel
         * @return
         */
        public Builder<B> addAutoJoinChannel(String channel) {
            getAutoJoinChannels().put(channel, "");
            return this;
        }

        /**
         * Utility method for
         * <code>{@link #getAutoJoinChannels().put(channel, key)</code>
         *
         * @param channel
         * @return
         */
        public Builder<B> addAutoJoinChannel(String channel, String key) {
            getAutoJoinChannels().put(channel, key);
            return this;
        }

        /**
         * Utility method to set server hostname and port
         *
         * @param hostname
         * @param port
         * @return
         */
        public Builder<B> setServer(String hostname, int port) {
            return setServerHostname(hostname).setServerPort(port);
        }

        /**
         * Utility method to set server hostname, port, and password
         *
         * @param hostname
         * @param port
         * @return
         */
        public Builder<B> setServer(String hostname, int port, String password) {
            return setServer(hostname, port).setServerPassword(password);
        }

        /**
         * Sets a new ListenerManager. <b>NOTE:</b> The {@link CoreHooks} are
         * added when this method is called. If you do not want this, remove
         * CoreHooks with
		 * {@link ListenerManager#removeListener(org.PircBotY.hooks.Listener) }
         *
         * @param listenerManager The listener manager
         */
        @SuppressWarnings("unchecked")
        public Builder<B> setListenerManager(ListenerManager<? extends B> listenerManager) {
            this.listenerManager = (ListenerManager<B>) listenerManager;
            for (Listener<B> curListener : this.listenerManager.getListeners()) {
                if (curListener instanceof CoreHooks) {
                    return this;
                }
            }
            listenerManager.addListener(new CoreHooks());
            return this;
        }

        /**
         * Returns the current ListenerManager in use by this bot. Note that the
         * default listener manager ({@link ListenerManager}) is lazy loaded
         * here unless one was already set
         *
         * @return Current ListenerManager
         */
        public ListenerManager<B> getListenerManager() {
            if (listenerManager == null) {
                setListenerManager(new ThreadedListenerManager<B>());
            }
            return listenerManager;
        }

        /**
         * Build a new configuration from this Builder
         *
         * @return
         */
        public Configuration<B> buildConfiguration() {
            return new Configuration<B>(this);
        }

        /**
         * Create a <b>new</b> builder with the specified hostname then build a
         * configuration. Useful for template builders
         *
         * @param hostname
         * @return
         */
        public Configuration<B> buildForServer(String hostname) {
            return new Builder<B>(this)
                    .setServerHostname(serverHostname)
                    .buildConfiguration();
        }

        /**
         * Create a <b>new</b> builder with the specified hostname and port then
         * build a configuration. Useful for template builders
         *
         * @param hostname
         * @return
         */
        public Configuration<B> buildForServer(String hostname, int port) {
            return new Builder<B>(this)
                    .setServerHostname(serverHostname)
                    .setServerPort(serverPort)
                    .buildConfiguration();
        }

        /**
         * Create a <b>new</b> builder with the specified hostname, port, and
         * password then build a configuration. Useful for template builders
         *
         * @param hostname
         * @return
         */
        public Configuration<B> buildForServer(String hostname, int port, String password) {
            return new Builder<B>(this)
                    .setServerHostname(serverHostname)
                    .setServerPort(serverPort)
                    .setServerPassword(serverPassword)
                    .buildConfiguration();
        }
    }

    /**
     * Factory for various bot classes.
     */
    public static class BotFactory {

        public UserChannelDao<User, Channel> createUserChannelDao(PircBotY bot) {
            return new UserChannelDao<User, Channel>(bot, bot.getConfiguration().getBotFactory());
        }

        public OutputRaw createOutputRaw(PircBotY bot) {
            return new OutputRaw(bot);
        }

        public OutputCAP createOutputCAP(PircBotY bot) {
            return new OutputCAP(bot);
        }

        public OutputIRC createOutputIRC(PircBotY bot) {
            return new OutputIRC(bot);
        }

        public OutputDCC createOutputDCC(PircBotY bot) {
            return new OutputDCC(bot);
        }

        public OutputChannel createOutputChannel(PircBotY bot, Channel channel) {
            return new OutputChannel(bot, channel);
        }

        public OutputUser createOutputUser(PircBotY bot, User user) {
            return new OutputUser(bot, user);
        }

        public InputParser createInputParser(PircBotY bot) {
            return new InputParser(bot);
        }

        public DccHandler createDccHandler(PircBotY bot) {
            return new DccHandler(bot);
        }

        public SendChat createSendChat(PircBotY bot, User user, Socket socket) throws IOException {
            return new SendChat(user, socket, bot.getConfiguration().getEncoding());
        }

        public ReceiveChat createReceiveChat(PircBotY bot, User user, Socket socket) throws IOException {
            return new ReceiveChat(user, socket, bot.getConfiguration().getEncoding());
        }

        public SendFileTransfer createSendFileTransfer(PircBotY bot, Socket socket, User user, File file, long startPosition) {
            return new SendFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
        }

        public ReceiveFileTransfer createReceiveFileTransfer(PircBotY bot, Socket socket, User user, File file, long startPosition) {
            return new ReceiveFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
        }

        public ServerInfo createServerInfo(PircBotY bot) {
            return new ServerInfo(bot);
        }

        public User createUser(PircBotY bot, String nick) {
            return new User(bot, bot.getUserChannelDao(), nick);
        }

        public Channel createChannel(PircBotY bot, String name) {
            return new Channel(bot, bot.getUserChannelDao(), name);
        }
    }
}

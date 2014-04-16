/**
 * Copyright (C) 2010-2013 Leon Blakey <lord.quackstar at gmail.com>
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

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pircboty.cap.CapHandler;
import org.pircboty.cap.TLSCapHandler;
import org.pircboty.exception.IrcException;
import org.pircboty.hooks.events.ActionEvent;
import org.pircboty.hooks.events.ChannelInfoEvent;
import org.pircboty.hooks.events.ConnectEvent;
import org.pircboty.hooks.events.FingerEvent;
import org.pircboty.hooks.events.HalfOpEvent;
import org.pircboty.hooks.events.InviteEvent;
import org.pircboty.hooks.events.JoinEvent;
import org.pircboty.hooks.events.KickEvent;
import org.pircboty.hooks.events.MessageEvent;
import org.pircboty.hooks.events.ModeEvent;
import org.pircboty.hooks.events.MotdEvent;
import org.pircboty.hooks.events.NickAlreadyInUseEvent;
import org.pircboty.hooks.events.NickChangeEvent;
import org.pircboty.hooks.events.NoticeEvent;
import org.pircboty.hooks.events.OpEvent;
import org.pircboty.hooks.events.OwnerEvent;
import org.pircboty.hooks.events.PartEvent;
import org.pircboty.hooks.events.PingEvent;
import org.pircboty.hooks.events.PrivateMessageEvent;
import org.pircboty.hooks.events.QuitEvent;
import org.pircboty.hooks.events.RemoveChannelBanEvent;
import org.pircboty.hooks.events.RemoveChannelKeyEvent;
import org.pircboty.hooks.events.RemoveChannelLimitEvent;
import org.pircboty.hooks.events.RemoveInviteOnlyEvent;
import org.pircboty.hooks.events.RemoveModeratedEvent;
import org.pircboty.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircboty.hooks.events.RemovePrivateEvent;
import org.pircboty.hooks.events.RemoveSecretEvent;
import org.pircboty.hooks.events.RemoveTopicProtectionEvent;
import org.pircboty.hooks.events.ServerPingEvent;
import org.pircboty.hooks.events.ServerResponseEvent;
import org.pircboty.hooks.events.SetChannelBanEvent;
import org.pircboty.hooks.events.SetChannelKeyEvent;
import org.pircboty.hooks.events.SetChannelLimitEvent;
import org.pircboty.hooks.events.SetInviteOnlyEvent;
import org.pircboty.hooks.events.SetModeratedEvent;
import org.pircboty.hooks.events.SetNoExternalMessagesEvent;
import org.pircboty.hooks.events.SetPrivateEvent;
import org.pircboty.hooks.events.SetSecretEvent;
import org.pircboty.hooks.events.SetTopicProtectionEvent;
import org.pircboty.hooks.events.SuperOpEvent;
import org.pircboty.hooks.events.TimeEvent;
import org.pircboty.hooks.events.TopicEvent;
import org.pircboty.hooks.events.UnknownEvent;
import org.pircboty.hooks.events.UserListEvent;
import org.pircboty.hooks.events.UserModeEvent;
import org.pircboty.hooks.events.VersionEvent;
import org.pircboty.hooks.events.VoiceEvent;
import org.pircboty.hooks.events.WhoisEvent;
import org.pircboty.snapshot.ChannelSnapshot;
import org.pircboty.snapshot.UserChannelDaoSnapshot;
import org.pircboty.snapshot.UserSnapshot;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Parse received input from IRC server.
 *
 * @author Leon Blakey <lord.quackstar at gmail.com>
 */
@RequiredArgsConstructor
@Slf4j
public class InputParser implements Closeable {

    public static final Marker INPUT_MARKER = MarkerFactory.getMarker("PircBotY.input");
    /**
     * Codes that say we are connected: Initial connection (001-4), user stats
     * (251-5), or MOTD (375-6).
     */
    protected static final ImmutableList<String> CONNECT_CODES = ImmutableList.of("001", "002", "003", "004", "005",
            "251", "252", "253", "254", "255", "375", "376");
    protected static final ImmutableList<ChannelModeHandler> DEFAULT_CHANNEL_MODE_HANDLERS;

    static {
        DEFAULT_CHANNEL_MODE_HANDLERS = ImmutableList.<ChannelModeHandler>builder()
                .add(new OpChannelModeHandler('o', UserLevel.OP) {
                    @Override
                    public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                        Utils.dispatchEvent(bot, new OpEvent<PircBotY>(bot, channel, sourceUser, recipientUser, adding));
                    }
                })
                .add(new OpChannelModeHandler('v', UserLevel.VOICE) {
                    @Override
                    public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                        Utils.dispatchEvent(bot, new VoiceEvent<PircBotY>(bot, channel, sourceUser, recipientUser, adding));
                    }
                })
                .add(new OpChannelModeHandler('h', UserLevel.HALFOP) {
                    @Override
                    public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                        Utils.dispatchEvent(bot, new HalfOpEvent<PircBotY>(bot, channel, sourceUser, recipientUser, adding));
                    }
                })
                .add(new OpChannelModeHandler('a', UserLevel.SUPEROP) {
                    @Override
                    public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                        Utils.dispatchEvent(bot, new SuperOpEvent<PircBotY>(bot, channel, sourceUser, recipientUser, adding));
                    }
                })
                .add(new OpChannelModeHandler('q', UserLevel.OWNER) {
                    @Override
                    public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                        Utils.dispatchEvent(bot, new OwnerEvent<PircBotY>(bot, channel, sourceUser, recipientUser, adding));
                    }
                })
                .add(new ChannelModeHandler('k') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        if (adding) {
                            String key = params.next();
                            channel.setChannelKey(key);
                            if (dispatchEvent) {
                                Utils.dispatchEvent(bot, new SetChannelKeyEvent<PircBotY>(bot, channel, sourceUser, key));
                            }
                        } else {
                            String key = params.hasNext() ? params.next() : null;
                            channel.setChannelKey(null);
                            if (dispatchEvent) {
                                Utils.dispatchEvent(bot, new RemoveChannelKeyEvent<PircBotY>(bot, channel, sourceUser, key));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('l') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        if (adding) {
                            int limit = Integer.parseInt(params.next());
                            channel.setChannelLimit(limit);
                            if (dispatchEvent) {
                                Utils.dispatchEvent(bot, new SetChannelLimitEvent<PircBotY>(bot, channel, sourceUser, limit));
                            }
                        } else {
                            channel.setChannelLimit(-1);
                            if (dispatchEvent) {
                                Utils.dispatchEvent(bot, new RemoveChannelLimitEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('b') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetChannelBanEvent<PircBotY>(bot, channel, sourceUser, params.next()));
                            } else {
                                Utils.dispatchEvent(bot, new RemoveChannelBanEvent<PircBotY>(bot, channel, sourceUser, params.next()));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('t') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        channel.setTopicProtection(adding);
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetTopicProtectionEvent<PircBotY>(bot, channel, sourceUser));
                            } else {
                                Utils.dispatchEvent(bot, new RemoveTopicProtectionEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('n') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        channel.setNoExternalMessages(adding);
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetNoExternalMessagesEvent<PircBotY>(bot, channel, sourceUser));
                            } else {
                                Utils.dispatchEvent(bot, new RemoveNoExternalMessagesEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('i') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        channel.setInviteOnly(adding);
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetInviteOnlyEvent<PircBotY>(bot, channel, sourceUser));
                            } else {
                                Utils.dispatchEvent(bot, new RemoveInviteOnlyEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('m') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        channel.setModerated(adding);
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetModeratedEvent<PircBotY>(bot, channel, sourceUser));
                            } else {
                                Utils.dispatchEvent(bot, new RemoveModeratedEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('p') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        channel.setChannelPrivate(adding);
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetPrivateEvent<PircBotY>(bot, channel, sourceUser));
                            } else {
                                Utils.dispatchEvent(bot, new RemovePrivateEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .add(new ChannelModeHandler('s') {
                    @Override
                    public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
                        channel.setSecret(adding);
                        if (dispatchEvent) {
                            if (adding) {
                                Utils.dispatchEvent(bot, new SetSecretEvent<PircBotY>(bot, channel, sourceUser));
                            } else {
                                Utils.dispatchEvent(bot, new RemoveSecretEvent<PircBotY>(bot, channel, sourceUser));
                            }
                        }
                    }
                })
                .build();
    }
    protected final Configuration<PircBotY> configuration;
    protected final PircBotY bot;
    protected final List<CapHandler> capHandlersFinished = new ArrayList<CapHandler>();
    protected boolean capEndSent = false;
    protected BufferedReader inputReader;
    //Builders
    protected final Map<String, WhoisEvent.Builder<PircBotY>> whoisBuilder = Maps.newHashMap();
    protected StringBuilder motdBuilder;
    @Getter
    protected boolean channelListRunning = false;
    protected ImmutableList.Builder<ChannelListEntry> channelListBuilder;
    protected int nickSuffix = 0;

    public InputParser(PircBotY bot) {
        this.bot = bot;
        this.configuration = bot.getConfiguration();
    }

    /**
     * This method handles events when any line of text arrives from the server,
     * then dispatching the appropriate event.
     *
     * @param line The raw line of text from the server.
     */
    public void handleLine(@NonNull String line) throws IOException, IrcException {
        log.info(INPUT_MARKER, line);

        List<String> parsedLine = Utils.tokenizeLine(line);

        String senderInfo = "";
        if (parsedLine.get(0).charAt(0) == ':') {
            senderInfo = parsedLine.remove(0);
        }

        String command = parsedLine.remove(0).toUpperCase(configuration.getLocale());

        // Check for server pings.
        if (command.equals("PING")) {
            // Respond to the ping and return immediately.
            configuration.getListenerManager().dispatchEvent(new ServerPingEvent<PircBotY>(bot, parsedLine.get(0)));
            return;
        } else if (command.startsWith("ERROR")) {
            //Server is shutting us down
            bot.shutdown(true);
            return;
        }

        String sourceNick;
        String sourceLogin = "";
        String sourceHostname = "";
        String target = !parsedLine.isEmpty() ? parsedLine.get(0) : "";

        if (target.startsWith(":")) {
            target = target.substring(1);
        }

        int exclamation = senderInfo.indexOf('!');
        int at = senderInfo.indexOf('@');
        if (senderInfo.startsWith(":")) {
            if (exclamation > 0 && at > 0 && exclamation < at) {
                sourceNick = senderInfo.substring(1, exclamation);
                sourceLogin = senderInfo.substring(exclamation + 1, at);
                sourceHostname = senderInfo.substring(at + 1);
            } else {
                int code = Utils.tryParseInt(command, -1);
                if (code != -1) {
                    if (!bot.loggedIn) {
                        processConnect(line, command, target, parsedLine);
                    }
                    processServerResponse(code, line, parsedLine);
                    // Return from the method.
                    return;
                } else // This is not a server response.
                // It must be a nick without login and hostname.
                // (or maybe a NOTICE or suchlike from the server)
                //WARNING: Changed from origional PircBot. Instead of command as target, use channel/user (setup later)
                {
                    sourceNick = senderInfo;
                }
            }
        } else {
            // We don't know what this line means.
            configuration.getListenerManager().dispatchEvent(new UnknownEvent<PircBotY>(bot, line));
            if (!bot.loggedIn) //Pass to CapHandlers, could be important
            {
                for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                    if (curCapHandler.handleUnknown(bot, line)) {
                        capHandlersFinished.add(curCapHandler);
                    }
                }
            }
            // Return from the method;
            return;
        }

        if (sourceNick.startsWith(":")) {
            sourceNick = sourceNick.substring(1);
        }

        if (!bot.loggedIn) {
            processConnect(line, command, target, parsedLine);
        }
        processCommand(target, sourceNick, sourceLogin, sourceHostname, command, line, parsedLine);
    }

    /**
     * Process any lines relevant to connect. Only called before bot is logged
     * into the server
     *
     * @param rawLine Raw, unprocessed line from the server
     * @param code
     * @param target
     * @param parsedLine Processed line
     * @throws IrcException If the server rejects the bot (nick already in use
     * or a 4** or 5** code
     * @throws IOException If an error occurs during upgrading to SSL
     */
    public void processConnect(String rawLine, String code, String target, List<String> parsedLine) throws IrcException, IOException {
        if (CONNECT_CODES.contains(code)) {
            // We're connected to the server.
            bot.loggedIn(configuration.getName() + (nickSuffix == 0 ? "" : nickSuffix));
            log.debug("Logged onto server.");

            configuration.getListenerManager().dispatchEvent(new ConnectEvent<PircBotY>(bot));

            //Handle automatic on connect stuff
            if (configuration.getNickservPassword() != null) {
                bot.sendIRC().identify(configuration.getNickservPassword());
            }
            ImmutableMap<String, String> autoConnectChannels = bot.reconnectChannels();
            if (autoConnectChannels == null) {
                autoConnectChannels = configuration.getAutoJoinChannels();
            }
            for (Map.Entry<String, String> channelEntry : autoConnectChannels.entrySet()) {
                bot.sendIRC().joinChannel(channelEntry.getKey(), channelEntry.getValue());
            }
        } else if (code.equals("433")) {
            //EXAMPLE: * AnAlreadyUsedName :Nickname already in use
            //Nickname in use, rename
            String usedNick = parsedLine.get(1);
            boolean autoNickChange = configuration.isAutoNickChange();
            String autoNewNick = null;
            if (autoNickChange) {
                nickSuffix++;
                bot.sendIRC().changeNick(autoNewNick = configuration.getName() + nickSuffix);
            }
            configuration.getListenerManager().dispatchEvent(new NickAlreadyInUseEvent<PircBotY>(bot, usedNick, autoNewNick, autoNickChange));
        } else if (code.equals("439")) {
            //EXAMPLE: PircBotY: Target change too fast. Please wait 104 seconds
            // No action required.
        } else if (configuration.isCapEnabled() && code.equals("451") && target.equals("CAP")) {
            //EXAMPLE: 451 CAP :You have not registered
            //Ignore, this is from servers that don't support CAP
        } else if (code.startsWith("5") || code.startsWith("4")) {
            throw new IrcException(IrcException.Reason.CannotLogin, "Received error: " + rawLine);
        } else if (code.equals("670")) {
            //Server is saying that we can upgrade to TLS
            SSLSocketFactory sslSocketFactory = ((SSLSocketFactory) SSLSocketFactory.getDefault());
            for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                if (curCapHandler instanceof TLSCapHandler) {
                    sslSocketFactory = ((TLSCapHandler) curCapHandler).getSslSocketFactory();
                }
            }
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(
                    bot.getSocket(),
                    bot.getLocalAddress().getHostAddress(),
                    bot.getSocket().getPort(),
                    true);
            sslSocket.startHandshake();
            bot.changeSocket(sslSocket);
            //Notify CAP Handlers
            for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                curCapHandler.handleUnknown(bot, rawLine);
            }
        } else if (code.equals("CAP")) {
            //Handle CAP Code; remove extra from params
            String capCommand = parsedLine.get(1);
            ImmutableList<String> capParams = ImmutableList.copyOf(StringUtils.split(parsedLine.get(2)));
            if (capCommand.equals("LS")) {
                for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                    log.debug("Executing cap handler " + curCapHandler);
                    if (curCapHandler.handleLS(bot, capParams)) {
                        log.debug("Cap handler " + curCapHandler + " finished");
                        capHandlersFinished.add(curCapHandler);
                    }
                }
            } else if (capCommand.equals("ACK")) {
                //Server is enabling a capability, store that
                bot.getEnabledCapabilities().addAll(capParams);

                for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                    if (curCapHandler.handleACK(bot, capParams)) {
                        log.trace("Removing cap handler " + curCapHandler);
                        capHandlersFinished.add(curCapHandler);
                    }
                }
            } else if (capCommand.equals("NAK")) {
                for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                    if (curCapHandler.handleNAK(bot, capParams)) {
                        capHandlersFinished.add(curCapHandler);
                    }
                }
            } else //Maybe the CapHandlers know how to use it
            {
                for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                    if (curCapHandler.handleUnknown(bot, rawLine)) {
                        capHandlersFinished.add(curCapHandler);
                    }
                }
            }
        } else //Pass to CapHandlers, could be important
        {
            for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                if (curCapHandler.handleUnknown(bot, rawLine)) {
                    capHandlersFinished.add(curCapHandler);
                }
            }
        }

        //Send CAP END if all CapHandlers are finished
        if (configuration.isCapEnabled() && !capEndSent && capHandlersFinished.containsAll(configuration.getCapHandlers())) {
            capEndSent = true;
            bot.sendCAP().end();
            bot.enabledCapabilities = Collections.unmodifiableList(bot.enabledCapabilities);
        }
    }

    public void processCommand(String target, String sourceNick, String sourceLogin, String sourceHostname, String command, String line, List<String> parsedLine) throws IOException {
        User source = bot.getUserChannelDao().getUser(sourceNick);
        //If the channel matches a prefix, then its a channel
        Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0) ? bot.getUserChannelDao().getChannel(target) : null;
        String message = parsedLine.size() >= 2 ? parsedLine.get(1) : "";

        // Check for CTCP requests.
        if (command.equals("PRIVMSG") && message.startsWith("\u0001") && message.endsWith("\u0001")) {
            String request = message.substring(1, message.length() - 1);
            if (request.equals("VERSION")) // VERSION request
            {
                configuration.getListenerManager().dispatchEvent(new VersionEvent<PircBotY>(bot, source, channel));
            } else if (request.startsWith("ACTION ")) // ACTION request
            {
                configuration.getListenerManager().dispatchEvent(new ActionEvent<PircBotY>(bot, source, channel, request.substring(7)));
            } else if (request.startsWith("PING ")) // PING request
            {
                configuration.getListenerManager().dispatchEvent(new PingEvent<PircBotY>(bot, source, channel, request.substring(5)));
            } else if (request.equals("TIME")) // TIME request
            {
                configuration.getListenerManager().dispatchEvent(new TimeEvent<PircBotY>(bot, channel, source));
            } else if (request.equals("FINGER")) // FINGER request
            {
                configuration.getListenerManager().dispatchEvent(new FingerEvent<PircBotY>(bot, source, channel));
            } else if (request.startsWith("DCC ")) {
                // This is a DCC request.
                boolean success = bot.getDccHandler().processDcc(source, request);
                if (!success) // The DccManager didn't know what to do with the line.
                {
                    configuration.getListenerManager().dispatchEvent(new UnknownEvent<PircBotY>(bot, line));
                }
            } else // An unknown CTCP message - ignore it.
            {
                configuration.getListenerManager().dispatchEvent(new UnknownEvent<PircBotY>(bot, line));
            }
        } else if (command.equals("PRIVMSG") && channel != null) // This is a normal message to a channel.
        {
            configuration.getListenerManager().dispatchEvent(new MessageEvent<PircBotY>(bot, channel, source, message));
        } else if (command.equals("PRIVMSG")) {
            // This is a private message to us.
            //Add to private message
            bot.getUserChannelDao().addUserToPrivate(source);
            configuration.getListenerManager().dispatchEvent(new PrivateMessageEvent<PircBotY>(bot, source, message));
        } else if (command.equals("JOIN")) {
            // Someone is joining a channel.
            if (sourceNick.equalsIgnoreCase(bot.getNick())) {
                //Its us, get channel info
                bot.sendRaw().rawLine("WHO " + target);
                bot.sendRaw().rawLine("MODE " + target);
            }
            source.setLogin(sourceLogin);
            source.setHostmask(sourceHostname);
            bot.getUserChannelDao().addUserToChannel(source, channel);
            configuration.getListenerManager().dispatchEvent(new JoinEvent<PircBotY>(bot, channel, source));
        } else if (command.equals("PART")) {
            // Someone is parting from a channel.
            UserChannelDaoSnapshot daoSnapshot = bot.getUserChannelDao().createSnapshot();
            ChannelSnapshot channelSnapshot = daoSnapshot.getChannel(channel.getName());
            UserSnapshot sourceSnapshot = daoSnapshot.getUser(source.getNick());
            if (sourceNick.equals(bot.getNick())) //We parted the channel
            {
                bot.getUserChannelDao().removeChannel(channel);
            } else //Just remove the user from memory
            {
                bot.getUserChannelDao().removeUserFromChannel(source, channel);
            }
            configuration.getListenerManager().dispatchEvent(new PartEvent<PircBotY>(bot, daoSnapshot, channelSnapshot, sourceSnapshot, message));
        } else if (command.equals("NICK")) {
            // Somebody is changing their nick.
            String newNick = target;
            bot.getUserChannelDao().renameUser(source, newNick);
            if (sourceNick.equals(bot.getNick())) // Update our nick if it was us that changed nick.
            {
                bot.setNick(newNick);
            }
            configuration.getListenerManager().dispatchEvent(new NickChangeEvent<PircBotY>(bot, sourceNick, newNick, source));
        } else if (command.equals("NOTICE")) // Someone is sending a notice.
        {
            configuration.getListenerManager().dispatchEvent(new NoticeEvent<PircBotY>(bot, source, channel, message));
        } else if (command.equals("QUIT")) {
            UserChannelDaoSnapshot daoSnapshot = bot.getUserChannelDao().createSnapshot();
            UserSnapshot sourceSnapshot = daoSnapshot.getUser(source.getNick());
            //A real target is missing, so index is off
            String reason = target;
            // Someone has quit from the IRC server.
            if (!sourceNick.equals(bot.getNick())) //Someone else
            {
                bot.getUserChannelDao().removeUser(source);
            }
            configuration.getListenerManager().dispatchEvent(new QuitEvent<PircBotY>(bot, daoSnapshot, sourceSnapshot, reason));
        } else if (command.equals("KICK")) {
            // Somebody has been kicked from a channel.
            User recipient = bot.getUserChannelDao().getUser(message);

            if (recipient.getNick().equals(bot.getNick())) //We were just kicked
            {
                bot.getUserChannelDao().removeChannel(channel);
            } else //Someone else
            {
                bot.getUserChannelDao().removeUserFromChannel(recipient, channel);
            }
            configuration.getListenerManager().dispatchEvent(new KickEvent<PircBotY>(bot, channel, source, recipient, parsedLine.get(2)));
        } else if (command.equals("MODE")) {
            // Somebody is changing the mode on a channel or user (Use long form since mode isn't after a : )
            String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
            if (mode.startsWith(":")) {
                mode = mode.substring(1);
            }
            processMode(source, target, mode);
        } else if (command.equals("TOPIC")) {
            // Someone is changing the topic.
            long currentTime = System.currentTimeMillis();
            String oldTopic = channel.getTopic();
            channel.setTopic(message);
            channel.setTopicSetter(sourceNick);
            channel.setTopicTimestamp(currentTime);

            configuration.getListenerManager().dispatchEvent(new TopicEvent<PircBotY>(bot, channel, oldTopic, message, source, currentTime, true));
        } else if (command.equals("INVITE")) {
            // Somebody is inviting somebody else into a channel.
            //Use line method instead of channel since channel is wrong
            configuration.getListenerManager().dispatchEvent(new InviteEvent<PircBotY>(bot, sourceNick, message));
            if (bot.getUserChannelDao().getChannels(source).isEmpty()) {
                bot.getUserChannelDao().removeUser(source);
            }
        } else if (command.equals("AWAY")) //IRCv3 AWAY notify
        {
            source.setAwayMessage(parsedLine.get(0));
        } else // If we reach this point, then we've found something that the PircBotY
        // Doesn't currently deal with.
        {
            configuration.getListenerManager().dispatchEvent(new UnknownEvent<PircBotY>(bot, line));
        }
    }

    /**
     * This method is called by the PircBotY when a numeric response is received
     * from the IRC server. We use this method to allow PircBotY to process
     * various responses from the server before then passing them on to the
     * onServerResponse method.
     * <p>
     * Note that this method is private and should not appear in any of the
     * javadoc generated documentation.
     *
     * @param code The three-digit numerical code for the response.
     * @param response The full response from the IRC server.
     */
    public void processServerResponse(int code, String rawResponse, List<String> parsedResponseOrig) {
        ImmutableList<String> parsedResponse = ImmutableList.copyOf(parsedResponseOrig);
        //Parsed response format: Everything after code
        //eg: Response 321 Channel :Users Name gives us [Channel, Users Name]
        ReplyConstants reply = ReplyConstants.getReplyConstant(code);
        switch (reply) {
            case RPL_LISTSTART: {
                //EXAMPLE: 321 Channel :Users Name (actual text)
                //A channel list is about to be sent
                channelListBuilder = ImmutableList.builder();
                channelListRunning = true;
                break;
            }
            case RPL_LIST: {
                //This is part of a full channel listing as part of /LIST
                //EXAMPLE: 322 lordquackstar #xomb 12 :xomb exokernel project @ www.xomb.org
                String channel = parsedResponse.get(1);
                int userCount = Utils.tryParseInt(parsedResponse.get(2), -1);
                String topic = parsedResponse.get(3);
                channelListBuilder.add(new ChannelListEntry(channel, userCount, topic));
                break;
            }
            case RPL_LISTEND: {
                //EXAMPLE: 323 :End of /LIST
                //End of channel list, dispatch event
                configuration.getListenerManager().dispatchEvent(new ChannelInfoEvent<PircBotY>(bot, channelListBuilder.build()));
                channelListBuilder = null;
                channelListRunning = false;
                break;
            }
            case RPL_TOPIC: {
                //EXAMPLE: 332 PircBotY #aChannel :I'm some random topic
                //This is topic about a channel we've just joined. From /JOIN or /TOPIC
                Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                String topic = parsedResponse.get(2);

                channel.setTopic(topic);
                break;
            }
            case RPL_TOPICINFO: {
                //EXAMPLE: 333 PircBotY #aChannel ISetTopic 1564842512
                //This is information on the topic of the channel we've just joined. From /JOIN or /TOPIC
                Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                User setBy = bot.getUserChannelDao().getUser(parsedResponse.get(2));
                long date = Utils.tryParseLong(parsedResponse.get(3), -1);

                channel.setTopicTimestamp(date * 1000);
                channel.setTopicSetter(setBy.getNick());

                configuration.getListenerManager().dispatchEvent(new TopicEvent<PircBotY>(bot, channel, null, channel.getTopic(), setBy, date, false));
                break;
            }
            case RPL_WHOREPLY: {
                //EXAMPLE: 352 PircBotY #aChannel ~someName 74.56.56.56.my.Hostmask wolfe.freenode.net someNick H :0 Full Name
                //Part of a WHO reply on information on individual users
                Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));

                //Setup user
                User curUser = bot.getUserChannelDao().getUser(parsedResponse.get(5));
                curUser.setLogin(parsedResponse.get(2));
                curUser.setHostmask(parsedResponse.get(3));
                curUser.setServer(parsedResponse.get(4));
                curUser.setNick(parsedResponse.get(5));
                processUserStatus(channel, curUser, parsedResponse.get(6));
                //Extra parsing needed since tokenizer stopped at :
                String rawEnding = parsedResponse.get(7);
                int rawEndingSpaceIndex = rawEnding.indexOf(' ');
                if (rawEndingSpaceIndex == -1) {
                    //parsedResponse data is trimmed, so if the index == -1, then there was no real name given and the space separating hops from real name was trimmed.
                    curUser.setHops(Integer.parseInt(rawEnding));
                    curUser.setRealName("");
                } else {
                    //parsedResponse data contains a real name
                    curUser.setHops(Integer.parseInt(rawEnding.substring(0, rawEndingSpaceIndex)));
                    curUser.setRealName(rawEnding.substring(rawEndingSpaceIndex + 1));
                }

                //Associate with channel
                bot.getUserChannelDao().addUserToChannel(curUser, channel);
                break;
            }
            case RPL_ENDOFWHO: {
                //EXAMPLE: 315 PircBotY #aChannel :End of /WHO list
                //End of the WHO reply
                Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                configuration.getListenerManager().dispatchEvent(new UserListEvent<PircBotY>(bot, channel, bot.getUserChannelDao().getUsers(channel)));
                break;
            }
            case RPL_CHANNELMODEIS: {
                //EXAMPLE: 324 PircBotY #aChannel +cnt
                //Full channel mode (In response to MODE <channel>)
                Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                ImmutableList<String> modeParsed = parsedResponse.subList(2, parsedResponse.size());
                String mode = StringUtils.join(modeParsed, ' ');

                channel.setMode(mode, modeParsed);
                configuration.getListenerManager().dispatchEvent(new ModeEvent<PircBotY>(bot, channel, null, mode, modeParsed));
                break;
            }
            case RPL_MOTDSTART: //Example: 375 PircBotY :- wolfe.freenode.net Message of the Day -
            //Motd is starting, reset the StringBuilder
            {
                motdBuilder = new StringBuilder();
                break;
            }
            case RPL_MOTD: //Example: 372 PircBotY :- Welcome to wolfe.freenode.net in Manchester, England, Uk!  Thanks to
            //This is part of the MOTD, add a new line
            {
                motdBuilder.append(CharMatcher.WHITESPACE.trimFrom(parsedResponse.get(1).substring(1))).append("\n");
                break;
            }
            case RPL_ENDOFMOTD: {
                //Example: PircBotY :End of /MOTD command.
                //End of MOTD, clean it and dispatch MotdEvent
                ServerInfo serverInfo = bot.getServerInfo();
                serverInfo.setMotd(motdBuilder.toString().trim());
                motdBuilder = null;
                configuration.getListenerManager().dispatchEvent(new MotdEvent<PircBotY>(bot, serverInfo.getMotd()));
                break;
            }
            case RPL_WHOISUSER: {
                //Example: 311 TheLQ Plazma ~Plazma freenode/staff/plazma * :Plazma Rooolz!
                //New whois is starting
                String whoisNick = parsedResponse.get(1);

                WhoisEvent.Builder<PircBotY> builder = new WhoisEvent.Builder<PircBotY>();
                builder.setNick(whoisNick);
                builder.setLogin(parsedResponse.get(2));
                builder.setHostname(parsedResponse.get(3));
                builder.setRealname(parsedResponse.get(5));
                whoisBuilder.put(whoisNick, builder);
                break;
            }
            case RPL_AWAY: //Example: 301 PircBotYUser TheLQ_ :I'm away, sorry
            {
                bot.getUserChannelDao().getUser(parsedResponse.get(1)).setAwayMessage(parsedResponse.get(2));
                break;
            }
            case RPL_WHOISCHANNELS: {
                //Example: 319 TheLQ Plazma :+#freenode
                //Channel list from whois. Re-tokenize since they're after the :
                String whoisNick = parsedResponse.get(1);
                ImmutableList<String> parsedChannels = ImmutableList.copyOf(Utils.tokenizeLine(parsedResponse.get(2)));

                whoisBuilder.get(whoisNick).setChannels(parsedChannels);
                break;
            }
            case RPL_WHOISSERVER: {
                //Server info from whois
                //312 TheLQ Plazma leguin.freenode.net :Ume?, SE, EU
                String whoisNick = parsedResponse.get(1);

                whoisBuilder.get(whoisNick).setServer(parsedResponse.get(2));
                whoisBuilder.get(whoisNick).setServerInfo(parsedResponse.get(3));
                break;
            }
            case RPL_WHOISIDLE: {
                //Idle time from whois
                //317 TheLQ md_5 6077 1347373349 :seconds idle, signon time
                String whoisNick = parsedResponse.get(1);

                whoisBuilder.get(whoisNick).setIdleSeconds(Long.parseLong(parsedResponse.get(2)));
                whoisBuilder.get(whoisNick).setSignOnTime(Long.parseLong(parsedResponse.get(3)));
                break;
            }
            case RPL_ENDOFWHOIS: {
                //End of whois
                //318 TheLQ Plazma :End of /WHOIS list.
                String whoisNick = parsedResponse.get(1);

                configuration.getListenerManager().dispatchEvent(whoisBuilder.get(whoisNick).generateEvent(bot));
                whoisBuilder.remove(whoisNick);
                break;
            }
            default: {
                switch (code) {
                    case 329: {
                        //EXAMPLE: 329 lordquackstar #botters 1199140245
                        //Tells when channel was created. From /JOIN
                        Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                        int createDate = Utils.tryParseInt(parsedResponse.get(2), -1);

                        //Set in channel
                        channel.setCreateTimestamp(createDate);
                        break;
                    }
                    case 330: {
                        whoisBuilder.get(parsedResponse.get(1)).setRegisteredAs(parsedResponse.get(2));
                        break;
                    }
                    case 4:
                    case 5: {
                        //Example: 004 PircBotY sendak.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
                        //Server info line, remove ending comment and let ServerInfo class parse it
                        int endCommentIndex = rawResponse.lastIndexOf(" :");
                        if (endCommentIndex > 1) {
                            String endComment = rawResponse.substring(endCommentIndex + 2);
                            int lastIndex = parsedResponseOrig.size() - 1;
                            if (endComment.equals(parsedResponseOrig.get(lastIndex))) {
                                parsedResponseOrig.remove(lastIndex);
                            }
                        }
                        break;
                    }
                }
            }
            bot.getServerInfo().parse(code, parsedResponseOrig);
        }

        configuration.getListenerManager().dispatchEvent(new ServerResponseEvent<PircBotY>(bot, code, rawResponse, parsedResponse));
    }

    /**
     * Called when the mode of a channel is set. We process this in order to
     * call the appropriate onOp, onDeop, etc method before finally calling the
     * override-able onMode method.
     * <p>
     * Note that this method is private and is not intended to appear in the
     * javadoc generated documentation.
     *
     * @param target The channel or nick that the mode operation applies to.
     * @param sourceNick The nick of the user that set the mode.
     * @param sourceLogin The login of the user that set the mode.
     * @param sourceHostname The hostname of the user that set the mode.
     * @param mode The mode that has been set.
     */
    public void processMode(User user, String target, String mode) {
        if (configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0) {
            // The mode of a channel is being changed.
            Channel channel = bot.getUserChannelDao().getChannel(target);
            channel.parseMode(mode);
            ImmutableList<String> modeParsed = ImmutableList.copyOf(StringUtils.split(mode, ' '));
            PeekingIterator<String> params = Iterators.peekingIterator(modeParsed.iterator());

            //Process modes letter by letter, grabbing paramaters as needed
            boolean adding = true;
            String modeLetters = params.next();
            for (int i = 0; i < modeLetters.length(); i++) {
                char curModeChar = modeLetters.charAt(i);
                if (curModeChar == '+') {
                    adding = true;
                } else if (curModeChar == '-') {
                    adding = false;
                } else {
                    ChannelModeHandler modeHandler = configuration.getChannelModeHandlers().get(curModeChar);
                    if (modeHandler != null) {
                        modeHandler.handleMode(bot, channel, user, params, adding, true);
                    }
                }
            }
            configuration.getListenerManager().dispatchEvent(new ModeEvent<PircBotY>(bot, channel, user, mode, modeParsed));
        } else // The mode of a user is being changed.
        {
            configuration.getListenerManager().dispatchEvent(new UserModeEvent<PircBotY>(bot, user, bot.getUserChannelDao().getUser(target), mode));
        }
    }

    public void processUserStatus(Channel chan, User user, String prefix) {
        if (prefix.contains("@")) {
            bot.getUserChannelDao().addUserToLevel(UserLevel.OP, user, chan);
        }
        if (prefix.contains("+")) {
            bot.getUserChannelDao().addUserToLevel(UserLevel.VOICE, user, chan);
        }
        if (prefix.contains("%")) {
            bot.getUserChannelDao().addUserToLevel(UserLevel.HALFOP, user, chan);
        }
        if (prefix.contains("~")) {
            bot.getUserChannelDao().addUserToLevel(UserLevel.OWNER, user, chan);
        }
        if (prefix.contains("&")) {
            bot.getUserChannelDao().addUserToLevel(UserLevel.SUPEROP, user, chan);
        }
        //Assume here (H) if there is no G
        user.setAwayMessage(prefix.contains("G") ? "" : null);
        user.setIrcop(prefix.contains("*"));
    }

    /**
     * Clear out builders.
     */
    @Override
    public void close() {
        capEndSent = false;
        capHandlersFinished.clear();
        whoisBuilder.clear();
        motdBuilder = null;
        channelListRunning = false;
        channelListBuilder = null;

    }

    protected static abstract class OpChannelModeHandler extends ChannelModeHandler {

        protected final UserLevel level;

        public OpChannelModeHandler(char mode, UserLevel level) {
            super(mode);
            this.level = level;
        }

        @Override
        public void handleMode(PircBotY bot, Channel channel, User sourceUser, PeekingIterator<String> params, boolean adding, boolean dispatchEvent) {
            User recipient = bot.getUserChannelDao().getUser(params.next());
            if (adding) {
                bot.getUserChannelDao().addUserToLevel(level, recipient, channel);
            } else {
                bot.getUserChannelDao().removeUserFromLevel(level, recipient, channel);
            }

            if (dispatchEvent) {
                dispatchEvent(bot, channel, sourceUser, recipient, adding);
            }
        }

        public abstract void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding);
    }
}

package net.ae97.pircboty;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.ae97.pircboty.api.events.ActionEvent;
import net.ae97.pircboty.api.events.ChannelInfoEvent;
import net.ae97.pircboty.api.events.ConnectEvent;
import net.ae97.pircboty.api.events.FingerEvent;
import net.ae97.pircboty.api.events.HalfOpEvent;
import net.ae97.pircboty.api.events.InviteEvent;
import net.ae97.pircboty.api.events.JoinEvent;
import net.ae97.pircboty.api.events.KickEvent;
import net.ae97.pircboty.api.events.MessageEvent;
import net.ae97.pircboty.api.events.ModeEvent;
import net.ae97.pircboty.api.events.MotdEvent;
import net.ae97.pircboty.api.events.NickAlreadyInUseEvent;
import net.ae97.pircboty.api.events.NickChangeEvent;
import net.ae97.pircboty.api.events.NoticeEvent;
import net.ae97.pircboty.api.events.OpEvent;
import net.ae97.pircboty.api.events.OwnerEvent;
import net.ae97.pircboty.api.events.PartEvent;
import net.ae97.pircboty.api.events.PingEvent;
import net.ae97.pircboty.api.events.PrivateMessageEvent;
import net.ae97.pircboty.api.events.QuitEvent;
import net.ae97.pircboty.api.events.RemoveChannelBanEvent;
import net.ae97.pircboty.api.events.RemoveChannelKeyEvent;
import net.ae97.pircboty.api.events.RemoveChannelLimitEvent;
import net.ae97.pircboty.api.events.RemoveInviteOnlyEvent;
import net.ae97.pircboty.api.events.RemoveModeratedEvent;
import net.ae97.pircboty.api.events.RemoveNoExternalMessagesEvent;
import net.ae97.pircboty.api.events.RemovePrivateEvent;
import net.ae97.pircboty.api.events.RemoveSecretEvent;
import net.ae97.pircboty.api.events.RemoveTopicProtectionEvent;
import net.ae97.pircboty.api.events.ServerPingEvent;
import net.ae97.pircboty.api.events.ServerResponseEvent;
import net.ae97.pircboty.api.events.SetChannelBanEvent;
import net.ae97.pircboty.api.events.SetChannelKeyEvent;
import net.ae97.pircboty.api.events.SetChannelLimitEvent;
import net.ae97.pircboty.api.events.SetInviteOnlyEvent;
import net.ae97.pircboty.api.events.SetModeratedEvent;
import net.ae97.pircboty.api.events.SetNoExternalMessagesEvent;
import net.ae97.pircboty.api.events.SetPrivateEvent;
import net.ae97.pircboty.api.events.SetSecretEvent;
import net.ae97.pircboty.api.events.SetTopicProtectionEvent;
import net.ae97.pircboty.api.events.SuperOpEvent;
import net.ae97.pircboty.api.events.TimeEvent;
import net.ae97.pircboty.api.events.TopicEvent;
import net.ae97.pircboty.api.events.UnknownEvent;
import net.ae97.pircboty.api.events.UserListEvent;
import net.ae97.pircboty.api.events.UserModeEvent;
import net.ae97.pircboty.api.events.VersionEvent;
import net.ae97.pircboty.api.events.VoiceEvent;
import net.ae97.pircboty.api.events.WhoisEvent;
import net.ae97.pircboty.cap.CapHandler;
import net.ae97.pircboty.exception.IrcException;
import net.ae97.pircboty.snapshot.ChannelSnapshot;
import net.ae97.pircboty.snapshot.UserChannelDaoSnapshot;
import net.ae97.pircboty.snapshot.UserSnapshot;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class InputParser implements Closeable {

    private static final List<String> CONNECT_CODES = new ArrayList<>(Arrays.asList(new String[]{"001", "002", "003", "004", "005",
        "251", "252", "253", "254", "255", "375", "376"}));
    private static final List<ChannelModeHandler> DEFAULT_CHANNEL_MODE_HANDLERS;

    static {
        DEFAULT_CHANNEL_MODE_HANDLERS = new ArrayList<>();
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new OpChannelModeHandler('o', UserLevel.OP) {
            @Override
            public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                bot.getConfiguration().getListenerManager().dispatchEvent(new OpEvent(bot, channel, sourceUser, recipientUser, adding));
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new OpChannelModeHandler('v', UserLevel.VOICE) {
            @Override
            public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                bot.getConfiguration().getListenerManager().dispatchEvent(new VoiceEvent(bot, channel, sourceUser, recipientUser, adding));
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new OpChannelModeHandler('h', UserLevel.HALFOP) {
            @Override
            public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                bot.getConfiguration().getListenerManager().dispatchEvent(new HalfOpEvent(bot, channel, sourceUser, recipientUser, adding));
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new OpChannelModeHandler('a', UserLevel.SUPEROP) {
            @Override
            public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                bot.getConfiguration().getListenerManager().dispatchEvent(new SuperOpEvent(bot, channel, sourceUser, recipientUser, adding));
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new OpChannelModeHandler('q', UserLevel.OWNER) {
            @Override
            public void dispatchEvent(PircBotY bot, Channel channel, User sourceUser, User recipientUser, boolean adding) {
                bot.getConfiguration().getListenerManager().dispatchEvent(new OwnerEvent(bot, channel, sourceUser, recipientUser, adding));
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('k') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                if (adding) {
                    String key = params.next();
                    channel.setChannelKey(key);
                    if (dispatchEvent) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetChannelKeyEvent(bot, channel, sourceUser, key));
                    }
                } else {
                    String key = params.hasNext() ? params.next() : null;
                    channel.setChannelKey(null);
                    if (dispatchEvent) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveChannelKeyEvent(bot, channel, sourceUser, key));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('l') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                if (adding) {
                    int limit = Integer.parseInt(params.next());
                    channel.setChannelLimit(limit);
                    if (dispatchEvent) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetChannelLimitEvent(bot, channel, sourceUser, limit));
                    }
                } else {
                    channel.setChannelLimit(-1);
                    if (dispatchEvent) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveChannelLimitEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('b') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetChannelBanEvent(bot, channel, sourceUser, params.next()));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveChannelBanEvent(bot, channel, sourceUser, params.next()));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('t') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                channel.setTopicProtection(adding);
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetTopicProtectionEvent(bot, channel, sourceUser));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveTopicProtectionEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('n') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                channel.setNoExternalMessages(adding);
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetNoExternalMessagesEvent(bot, channel, sourceUser));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveNoExternalMessagesEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('i') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                channel.setInviteOnly(adding);
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetInviteOnlyEvent(bot, channel, sourceUser));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveInviteOnlyEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('m') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                channel.setModerated(adding);
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetModeratedEvent(bot, channel, sourceUser));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveModeratedEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('p') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                channel.setChannelPrivate(adding);
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetPrivateEvent(bot, channel, sourceUser));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemovePrivateEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
        DEFAULT_CHANNEL_MODE_HANDLERS.add(new ChannelModeHandler('s') {
            @Override
            public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
                channel.setSecret(adding);
                if (dispatchEvent) {
                    if (adding) {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new SetSecretEvent(bot, channel, sourceUser));
                    } else {
                        bot.getConfiguration().getListenerManager().dispatchEvent(new RemoveSecretEvent(bot, channel, sourceUser));
                    }
                }
            }
        });
    }
    private final Configuration<PircBotY> configuration;
    private final PircBotY bot;
    private final List<CapHandler> capHandlersFinished = new LinkedList<>();
    private boolean capEndSent = false;
    private BufferedReader inputReader;
    private final Map<String, WhoisEvent.Builder> whoisBuilder = new ConcurrentHashMap<>();
    private StringBuilder motdBuilder;
    private boolean channelListRunning = false;
    private List<ChannelListEntry> channelListBuilder;
    private int nickSuffix = 0;

    public InputParser(PircBotY bot) {
        this.bot = bot;
        this.configuration = bot.getConfiguration();
    }

    protected void handleLine(String line) throws IOException, IrcException {
        Validate.notNull(line);
        PircBotY.getLogger().info("<<<" + line);
        List<String> parsedLine = Utils.tokenizeLine(line);
        String senderInfo = "";
        if (parsedLine.get(0).charAt(0) == ':') {
            senderInfo = parsedLine.remove(0);
        }
        String command = parsedLine.remove(0).toUpperCase(configuration.getLocale());
        if (command.equals("PING")) {
            configuration.getListenerManager().dispatchEvent(new ServerPingEvent(bot, parsedLine.get(0)));
            return;
        } else if (command.startsWith("ERROR")) {
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
                    if (!bot.isLoggedIn()) {
                        processConnect(line, command, target, parsedLine);
                    }
                    processServerResponse(code, line, parsedLine);
                    return;
                } else {
                    sourceNick = senderInfo;
                }
            }
        } else {
            configuration.getListenerManager().dispatchEvent(new UnknownEvent(bot, line));
            if (!bot.isLoggedIn()) {
                for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                    if (curCapHandler.handleUnknown(bot, line)) {
                        capHandlersFinished.add(curCapHandler);
                    }
                }
            }
            return;
        }
        if (sourceNick.startsWith(":")) {
            sourceNick = sourceNick.substring(1);
        }
        if (!bot.isLoggedIn()) {
            processConnect(line, command, target, parsedLine);
        }
        processCommand(target, sourceNick, sourceLogin, sourceHostname, command, line, parsedLine);
    }

    protected void processConnect(String rawLine, String code, String target, List<String> parsedLine) throws IrcException, IOException {
        if (CONNECT_CODES.contains(code)) {
            bot.loggedIn(configuration.getName() + (nickSuffix == 0 ? "" : nickSuffix));
            PircBotY.getLogger().log(Level.FINE, "Logged onto server.");
            configuration.getListenerManager().dispatchEvent(new ConnectEvent(bot));
            if (configuration.getNickservPassword() != null) {
                bot.sendIRC().identify(configuration.getNickservPassword());
            }
            Map<String, String> autoConnectChannels = bot.reconnectChannels();
            if (autoConnectChannels == null) {
                autoConnectChannels = configuration.getAutoJoinChannels();
            }
            for (Map.Entry<String, String> channelEntry : autoConnectChannels.entrySet()) {
                bot.sendIRC().joinChannel(channelEntry.getKey(), channelEntry.getValue());
            }
        } else if (code.equals("433")) {
            String usedNick = parsedLine.get(1);
            boolean autoNickChange = configuration.isAutoNickChange();
            String autoNewNick = null;
            if (autoNickChange) {
                nickSuffix++;
                bot.sendIRC().changeNick(autoNewNick = configuration.getName() + nickSuffix);
            }
            configuration.getListenerManager().dispatchEvent(new NickAlreadyInUseEvent(bot, usedNick, autoNewNick, autoNickChange));
        } else if (code.equals("439")) {
        } else if (configuration.isCapEnabled() && code.equals("451") && target.equals("CAP")) {
        } else if (code.startsWith("5") || code.startsWith("4")) {
            throw new IrcException(IrcException.Reason.CannotLogin, "Received error: " + rawLine);
        } else if (code.equals("CAP")) {
            String capCommand = parsedLine.get(1);
            List<String> capParams = new ArrayList<>(Arrays.asList(StringUtils.split(parsedLine.get(2))));
            switch (capCommand) {
                case "LS":
                    for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                        PircBotY.getLogger().log(Level.FINE, "Executing cap handler " + curCapHandler);
                        if (curCapHandler.handleLS(bot, capParams)) {
                            PircBotY.getLogger().log(Level.FINE, "Cap handler " + curCapHandler + " finished");
                            capHandlersFinished.add(curCapHandler);
                        }
                    }
                    break;
                case "ACK":
                    bot.getEnabledCapabilities().addAll(capParams);
                    for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                        if (curCapHandler.handleACK(bot, capParams)) {
                            PircBotY.getLogger().log(Level.FINER, "Removing cap handler " + curCapHandler);
                            capHandlersFinished.add(curCapHandler);
                        }
                    }
                    break;
                case "NAK":
                    for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                        if (curCapHandler.handleNAK(bot, capParams)) {
                            capHandlersFinished.add(curCapHandler);
                        }
                    }
                    break;
                default:
                    for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                        if (curCapHandler.handleUnknown(bot, rawLine)) {
                            capHandlersFinished.add(curCapHandler);
                        }
                    }
                    break;
            }
        } else {
            for (CapHandler curCapHandler : configuration.getCapHandlers()) {
                if (curCapHandler.handleUnknown(bot, rawLine)) {
                    capHandlersFinished.add(curCapHandler);
                }
            }
        }
        if (configuration.isCapEnabled() && !capEndSent && capHandlersFinished.containsAll(configuration.getCapHandlers())) {
            capEndSent = true;
            bot.sendCAP().end();
        }
    }

    protected void processCommand(String target, String sourceNick, String sourceLogin, String sourceHostname, String command, String line, List<String> parsedLine) throws IOException {
        User source = bot.getUserChannelDao().getUser(sourceNick);
        Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0) ? bot.getUserChannelDao().getChannel(target) : null;
        String message = parsedLine.size() >= 2 ? parsedLine.get(1) : "";
        if (command.equals("PRIVMSG") && message.startsWith("\u0001") && message.endsWith("\u0001")) {
            String request = message.substring(1, message.length() - 1);
            if (request.equals("VERSION")) {
                configuration.getListenerManager().dispatchEvent(new VersionEvent(bot, source, channel));
            } else if (request.startsWith("ACTION ")) {
                configuration.getListenerManager().dispatchEvent(new ActionEvent(bot, source, channel, request.substring(7)));
            } else if (request.startsWith("PING ")) {
                configuration.getListenerManager().dispatchEvent(new PingEvent(bot, source, channel, request.substring(5)));
            } else if (request.equals("TIME")) {
                configuration.getListenerManager().dispatchEvent(new TimeEvent(bot, channel, source));
            } else if (request.equals("FINGER")) {
                configuration.getListenerManager().dispatchEvent(new FingerEvent(bot, source, channel));
            } else if (request.startsWith("DCC ")) {
                boolean success = bot.getDccHandler().processDcc(source, request);
                if (!success) {
                    configuration.getListenerManager().dispatchEvent(new UnknownEvent(bot, line));
                }
            } else {
                configuration.getListenerManager().dispatchEvent(new UnknownEvent(bot, line));
            }
        } else if (command.equals("PRIVMSG") && channel != null) {
            configuration.getListenerManager().dispatchEvent(new MessageEvent(bot, channel, source, message));
        } else if (command.equals("PRIVMSG")) {
            bot.getUserChannelDao().addUserToPrivate(source);
            configuration.getListenerManager().dispatchEvent(new PrivateMessageEvent(bot, source, message));
        } else if (command.equals("JOIN")) {
            if (sourceNick.equalsIgnoreCase(bot.getNick())) {
                bot.sendRaw().rawLine("WHO " + target);
                bot.sendRaw().rawLine("MODE " + target);
            }
            source.setLogin(sourceLogin);
            source.setHostmask(sourceHostname);
            bot.getUserChannelDao().addUserToChannel(source, channel);
            configuration.getListenerManager().dispatchEvent(new JoinEvent(bot, channel, source));
        } else if (command.equals("PART")) {
            UserChannelDao<PircBotY, UserSnapshot, ChannelSnapshot> daoSnapshot = bot.getUserChannelDao().createSnapshot();
            ChannelSnapshot channelSnapshot = daoSnapshot.getChannel(channel.getName());
            UserSnapshot sourceSnapshot = daoSnapshot.getUser(source.getNick());
            if (sourceNick.equals(bot.getNick())) {
                bot.getUserChannelDao().removeChannel(channel);
            } else {
                bot.getUserChannelDao().removeUserFromChannel(source, channel);
            }
            configuration.getListenerManager().dispatchEvent(new PartEvent(bot, daoSnapshot, channelSnapshot, sourceSnapshot, message));
        } else if (command.equals("NICK")) {
            String newNick = target;
            bot.getUserChannelDao().renameUser(source, newNick);
            if (sourceNick.equals(bot.getNick())) {
                bot.setNick(newNick);
            }
            configuration.getListenerManager().dispatchEvent(new NickChangeEvent(bot, sourceNick, newNick, source));
        } else if (command.equals("NOTICE")) {
            configuration.getListenerManager().dispatchEvent(new NoticeEvent(bot, source, channel, message));
        } else if (command.equals("QUIT")) {
            UserChannelDaoSnapshot<PircBotY> daoSnapshot = bot.getUserChannelDao().createSnapshot();
            UserSnapshot sourceSnapshot = daoSnapshot.getUser(source.getNick());
            String reason = target;
            if (!sourceNick.equals(bot.getNick())) {
                bot.getUserChannelDao().removeUser(source);
            }
            configuration.getListenerManager().dispatchEvent(new QuitEvent(bot, daoSnapshot, sourceSnapshot, reason));
        } else if (command.equals("KICK")) {
            User recipient = bot.getUserChannelDao().getUser(message);
            if (recipient.getNick().equals(bot.getNick())) {
                bot.getUserChannelDao().removeChannel(channel);
            } else {
                bot.getUserChannelDao().removeUserFromChannel(recipient, channel);
            }
            configuration.getListenerManager().dispatchEvent(new KickEvent(bot, channel, source, recipient, parsedLine.get(2)));
        } else if (command.equals("MODE")) {
            String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
            if (mode.startsWith(":")) {
                mode = mode.substring(1);
            }
            processMode(source, target, mode);
        } else if (command.equals("TOPIC")) {
            long currentTime = System.currentTimeMillis();
            String oldTopic = channel.getTopic();
            channel.setTopic(message);
            channel.setTopicSetter(sourceNick);
            channel.setTopicTimestamp(currentTime);
            configuration.getListenerManager().dispatchEvent(new TopicEvent(bot, channel, oldTopic, message, source, currentTime, true));
        } else if (command.equals("INVITE")) {
            configuration.getListenerManager().dispatchEvent(new InviteEvent(bot, sourceNick, message));
            if (bot.getUserChannelDao().getChannels(source).isEmpty()) {
                bot.getUserChannelDao().removeUser(source);
            }
        } else if (command.equals("AWAY")) {
            source.setAwayMessage(parsedLine.get(0));
        } else {
            configuration.getListenerManager().dispatchEvent(new UnknownEvent(bot, line));
        }
    }

    protected void processServerResponse(int code, String rawResponse, List<String> parsedResponseOrig) {
        List<String> parsedResponse = new ArrayList<>(parsedResponseOrig);
        ReplyConstants reply = ReplyConstants.getReplyConstant(code);
        if (reply != null) {
            switch (reply) {
                case RPL_LISTSTART: {
                    channelListBuilder = new LinkedList<>();
                    channelListRunning = true;
                    break;
                }
                case RPL_LIST: {
                    String channel = parsedResponse.get(1);
                    int userCount = Utils.tryParseInt(parsedResponse.get(2), -1);
                    String topic = parsedResponse.get(3);
                    channelListBuilder.add(new ChannelListEntry(channel, userCount, topic));
                    break;
                }
                case RPL_LISTEND: {
                    configuration.getListenerManager().dispatchEvent(new ChannelInfoEvent(bot, channelListBuilder));
                    channelListBuilder = null;
                    channelListRunning = false;
                    break;
                }
                case RPL_TOPIC: {
                    Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                    String topic = parsedResponse.get(2);
                    channel.setTopic(topic);
                    break;
                }
                case RPL_TOPICINFO: {
                    Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                    User setBy = bot.getUserChannelDao().getUser(parsedResponse.get(2));
                    long date = Utils.tryParseLong(parsedResponse.get(3), -1);
                    channel.setTopicTimestamp(date * 1000);
                    channel.setTopicSetter(setBy.getNick());
                    configuration.getListenerManager().dispatchEvent(new TopicEvent(bot, channel, null, channel.getTopic(), setBy, date, false));
                    break;
                }
                case RPL_WHOREPLY: {
                    Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                    User curUser = bot.getUserChannelDao().getUser(parsedResponse.get(5));
                    curUser.setLogin(parsedResponse.get(2));
                    curUser.setHostmask(parsedResponse.get(3));
                    curUser.setServer(parsedResponse.get(4));
                    curUser.setNick(parsedResponse.get(5));
                    processUserStatus(channel, curUser, parsedResponse.get(6));
                    String rawEnding = parsedResponse.get(7);
                    int rawEndingSpaceIndex = rawEnding.indexOf(' ');
                    if (rawEndingSpaceIndex == -1) {
                        curUser.setHops(Integer.parseInt(rawEnding));
                        curUser.setRealName("");
                    } else {
                        curUser.setHops(Integer.parseInt(rawEnding.substring(0, rawEndingSpaceIndex)));
                        curUser.setRealName(rawEnding.substring(rawEndingSpaceIndex + 1));
                    }
                    bot.getUserChannelDao().addUserToChannel(curUser, channel);
                    break;
                }
                case RPL_ENDOFWHO: {
                    Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                    configuration.getListenerManager().dispatchEvent(new UserListEvent(bot, channel, bot.getUserChannelDao().getUsers(channel)));
                    break;
                }
                case RPL_CHANNELMODEIS: {
                    Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                    List<String> modeParsed = parsedResponse.subList(2, parsedResponse.size());
                    String mode = StringUtils.join(modeParsed, ' ');
                    channel.setMode(mode, modeParsed);
                    configuration.getListenerManager().dispatchEvent(new ModeEvent(bot, channel, null, mode, modeParsed));
                    break;
                }
                case RPL_MOTDSTART: {
                    motdBuilder = new StringBuilder();
                    break;
                }
                case RPL_MOTD: {
                    motdBuilder.append((parsedResponse.get(1).substring(1)).trim()).append("\n");
                    break;
                }
                case RPL_ENDOFMOTD: {
                    ServerInfo serverInfo = bot.getServerInfo();
                    serverInfo.setMotd(motdBuilder.toString().trim());
                    motdBuilder = null;
                    configuration.getListenerManager().dispatchEvent(new MotdEvent(bot, serverInfo.getMotd()));
                    break;
                }
                case RPL_WHOISUSER: {
                    String whoisNick = parsedResponse.get(1);
                    WhoisEvent.Builder builder = new WhoisEvent.Builder();
                    builder.setNick(whoisNick);
                    builder.setLogin(parsedResponse.get(2));
                    builder.setHostname(parsedResponse.get(3));
                    builder.setRealname(parsedResponse.get(5));
                    whoisBuilder.put(whoisNick, builder);
                    break;
                }
                case RPL_AWAY: {
                    bot.getUserChannelDao().getUser(parsedResponse.get(1)).setAwayMessage(parsedResponse.get(2));
                    break;
                }
                case RPL_WHOISCHANNELS: {
                    String whoisNick = parsedResponse.get(1);
                    List<String> parsedChannels = Utils.tokenizeLine(parsedResponse.get(2));
                    whoisBuilder.get(whoisNick).setChannels(parsedChannels);
                    break;
                }
                case RPL_WHOISSERVER: {
                    String whoisNick = parsedResponse.get(1);
                    whoisBuilder.get(whoisNick).setServer(parsedResponse.get(2));
                    whoisBuilder.get(whoisNick).setServerInfo(parsedResponse.get(3));
                    break;
                }
                case RPL_WHOISIDLE: {
                    String whoisNick = parsedResponse.get(1);
                    whoisBuilder.get(whoisNick).setIdleSeconds(Long.parseLong(parsedResponse.get(2)));
                    whoisBuilder.get(whoisNick).setSignOnTime(Long.parseLong(parsedResponse.get(3)));
                    break;
                }
                case RPL_ENDOFWHOIS: {
                    String whoisNick = parsedResponse.get(1);
                    configuration.getListenerManager().dispatchEvent(whoisBuilder.get(whoisNick).generateEvent(bot));
                    whoisBuilder.remove(whoisNick);
                    break;
                }
            }
        } else {
            switch (code) {
                case 329: {
                    Channel channel = bot.getUserChannelDao().getChannel(parsedResponse.get(1));
                    int createDate = Utils.tryParseInt(parsedResponse.get(2), -1);
                    channel.setCreateTimestamp(createDate);
                    break;
                }
                case 330: {
                    whoisBuilder.get(parsedResponse.get(1)).setRegisteredAs(parsedResponse.get(2));
                    break;
                }
                case 4:
                case 5: {
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
        configuration.getListenerManager().dispatchEvent(new ServerResponseEvent(bot, code, rawResponse, parsedResponse));
    }

    protected void processMode(User user, String target, String mode) {
        if (configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0) {
            Channel channel = bot.getUserChannelDao().getChannel(target);
            channel.parseMode(mode);
            List<String> modeParsed = new ArrayList<>(Arrays.asList(StringUtils.split(mode, ' ')));
            Iterator<String> params = modeParsed.iterator();
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
            configuration.getListenerManager().dispatchEvent(new ModeEvent(bot, channel, user, mode, modeParsed));
        } else {
            configuration.getListenerManager().dispatchEvent(new UserModeEvent(bot, user, bot.getUserChannelDao().getUser(target), mode));
        }
    }

    protected void processUserStatus(Channel chan, User user, String prefix) {
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
        user.setAwayMessage(prefix.contains("G") ? "" : null);
        user.setIrcop(prefix.contains("*"));
    }

    @Override
    public void close() {
        capEndSent = false;
        capHandlersFinished.clear();
        whoisBuilder.clear();
        motdBuilder = null;
        channelListRunning = false;
        channelListBuilder = null;
    }

    public boolean isChannelListRunning() {
        return channelListRunning;
    }

    public static List<ChannelModeHandler> getDefaultChannelModeHandlers() {
        return DEFAULT_CHANNEL_MODE_HANDLERS;
    }

    private static abstract class OpChannelModeHandler extends ChannelModeHandler {

        private final UserLevel level;

        public OpChannelModeHandler(char mode, UserLevel level) {
            super(mode);
            this.level = level;
        }

        @Override
        public void handleMode(PircBotY bot, Channel channel, User sourceUser, Iterator<String> params, boolean adding, boolean dispatchEvent) {
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

package net.ae97.pircboty;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import net.ae97.pircboty.dcc.DccHandler;
import net.ae97.pircboty.dcc.ReceiveChat;
import net.ae97.pircboty.dcc.ReceiveFileTransfer;
import net.ae97.pircboty.dcc.SendChat;
import net.ae97.pircboty.dcc.SendFileTransfer;
import net.ae97.pircboty.output.OutputCAP;
import net.ae97.pircboty.output.OutputChannel;
import net.ae97.pircboty.output.OutputDCC;
import net.ae97.pircboty.output.OutputIRC;
import net.ae97.pircboty.output.OutputRaw;
import net.ae97.pircboty.output.OutputUser;

public abstract class BotFactory {

    public abstract UserChannelDao<PircBotY, User, Channel> createUserChannelDao(PircBotY bot);

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

    public abstract User createUser(PircBotY bot, String nick);

    public abstract Channel createChannel(PircBotY bot, String name);

    public static class DefaultBotFactory extends BotFactory {

        @Override
        public UserChannelDao<PircBotY, User, Channel> createUserChannelDao(PircBotY bot) {
            return new UserChannelDao<>(bot, bot.getConfiguration().getBotFactory(), PircBotY.class, User.class, Channel.class);
        }

        @Override
        public OutputChannel createOutputChannel(PircBotY bot, Channel channel) {
            return new OutputChannel(bot, channel);
        }

        @Override
        public OutputUser createOutputUser(PircBotY bot, User user) {
            return new OutputUser(bot, user);
        }

        @Override
        public InputParser createInputParser(PircBotY bot) {
            return new InputParser(bot);
        }

        @Override
        public DccHandler createDccHandler(PircBotY bot) {
            return new DccHandler(bot);
        }

        @Override
        public SendChat createSendChat(PircBotY bot, User user, Socket socket) throws IOException {
            return new SendChat(user, socket, bot.getConfiguration().getEncoding());
        }

        @Override
        public ReceiveChat createReceiveChat(PircBotY bot, User user, Socket socket) throws IOException {
            return new ReceiveChat(user, socket, bot.getConfiguration().getEncoding());
        }

        @Override
        public SendFileTransfer createSendFileTransfer(PircBotY bot, Socket socket, User user, File file, long startPosition) {
            return new SendFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
        }

        @Override
        public ReceiveFileTransfer createReceiveFileTransfer(PircBotY bot, Socket socket, User user, File file, long startPosition) {
            return new ReceiveFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
        }

        @Override
        public ServerInfo createServerInfo(PircBotY bot) {
            return new ServerInfo(bot);
        }

        @Override
        public User createUser(PircBotY bot, String nick) {
            return new User(bot, bot.getUserChannelDao(), nick);
        }

        @Override
        public Channel createChannel(PircBotY bot, String name) {
            return new Channel(bot, bot.getUserChannelDao(), name);
        }
    }
}

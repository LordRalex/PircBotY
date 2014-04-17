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

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import org.pircboty.dcc.DccHandler;
import org.pircboty.dcc.ReceiveChat;
import org.pircboty.dcc.ReceiveFileTransfer;
import org.pircboty.dcc.SendChat;
import org.pircboty.dcc.SendFileTransfer;
import org.pircboty.output.OutputCAP;
import org.pircboty.output.OutputChannel;
import org.pircboty.output.OutputDCC;
import org.pircboty.output.OutputIRC;
import org.pircboty.output.OutputRaw;
import org.pircboty.output.OutputUser;

/**
 * Factory for various bot classes.
 */
public abstract class BotFactory<P extends PircBotY, U extends User, C extends Channel> {

    public abstract UserChannelDao<U, C> createUserChannelDao(P bot);

    public OutputRaw createOutputRaw(P bot) {
        return new OutputRaw(bot);
    }

    public OutputCAP createOutputCAP(P bot) {
        return new OutputCAP(bot);
    }

    public OutputIRC createOutputIRC(P bot) {
        return new OutputIRC(bot);
    }

    public OutputDCC createOutputDCC(P bot) {
        return new OutputDCC(bot);
    }

    public OutputChannel createOutputChannel(P bot, C channel) {
        return new OutputChannel(bot, channel);
    }

    public OutputUser createOutputUser(P bot, U user) {
        return new OutputUser(bot, user);
    }

    public InputParser createInputParser(P bot) {
        return new InputParser(bot);
    }

    public DccHandler createDccHandler(P bot) {
        return new DccHandler(bot);
    }

    public SendChat createSendChat(P bot, U user, Socket socket) throws IOException {
        return new SendChat(user, socket, bot.getConfiguration().getEncoding());
    }

    public ReceiveChat createReceiveChat(P bot, U user, Socket socket) throws IOException {
        return new ReceiveChat(user, socket, bot.getConfiguration().getEncoding());
    }

    public SendFileTransfer createSendFileTransfer(P bot, Socket socket, U user, File file, long startPosition) {
        return new SendFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
    }

    public ReceiveFileTransfer createReceiveFileTransfer(P bot, Socket socket, U user, File file, long startPosition) {
        return new ReceiveFileTransfer(bot.getConfiguration(), socket, user, file, startPosition);
    }

    public ServerInfo createServerInfo(P bot) {
        return new ServerInfo(bot);
    }

    public abstract U createUser(P bot, String nick);

    public abstract C createChannel(P bot, String name);

    public static class DefaultBotFactory extends BotFactory<PircBotY, User, Channel> {

        @Override
        public UserChannelDao<User, Channel> createUserChannelDao(PircBotY bot) {
            return new UserChannelDao<User, Channel>(bot, bot.getConfiguration().getBotFactory());
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

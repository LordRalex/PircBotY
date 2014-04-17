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
package org.pircboty.hooks.events;

import com.google.common.collect.ImmutableList;
import org.pircboty.PircBotY;
import org.pircboty.hooks.Event;

/**
 * Dispatched when we receive a completed Whois request. Note this is completely
 * independent of User and Channel objects since a user might not be connected
 * to us directly
 *
 * @author
 */
//TODO: Add tests
public class WhoisEvent<B extends PircBotY> extends Event<B> {

    private final String nick;
    private final String login;
    private final String hostname;
    private final String realname;
    private final ImmutableList<String> channels;
    private final String server;
    private final String serverInfo;
    private final long idleSeconds;
    private final long signOnTime;
    private final String registeredAs;

    public WhoisEvent(B bot, Builder<B> builder) {
        super(bot);
        this.nick = builder.getNick();
        this.login = builder.getLogin();
        this.hostname = builder.getHostname();
        this.realname = builder.getRealname();
        this.channels = builder.getChannels();
        this.server = builder.getServer();
        this.serverInfo = builder.getServerInfo();
        this.idleSeconds = builder.getIdleSeconds();
        this.signOnTime = builder.getSignOnTime();
        this.registeredAs = builder.getRegisteredAs();
    }

    @Override
    public void respond(String response) {
        getBot().sendIRC().message(getNick(), response);
    }

    public static class Builder<B extends PircBotY> {

        private String nick;
        private String login;
        private String hostname;
        private String realname;
        private ImmutableList<String> channels;
        private String server;
        private String serverInfo;
        private long idleSeconds;
        private long signOnTime;
        private String registeredAs;

        public WhoisEvent<B> generateEvent(B bot) {
            return new WhoisEvent<B>(bot, this);
        }

        public String getNick() {
            return nick;
        }

        public String getLogin() {
            return login;
        }

        public String getHostname() {
            return hostname;
        }

        public String getRealname() {
            return realname;
        }

        public ImmutableList<String> getChannels() {
            return channels;
        }

        public String getServer() {
            return server;
        }

        public String getServerInfo() {
            return serverInfo;
        }

        public long getIdleSeconds() {
            return idleSeconds;
        }

        public long getSignOnTime() {
            return signOnTime;
        }

        public String getRegisteredAs() {
            return registeredAs;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public void setChannels(ImmutableList<String> channels) {
            this.channels = channels;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public void setServerInfo(String serverInfo) {
            this.serverInfo = serverInfo;
        }

        public void setIdleSeconds(long idleSeconds) {
            this.idleSeconds = idleSeconds;
        }

        public void setSignOnTime(long signOnTime) {
            this.signOnTime = signOnTime;
        }

        public void setRegisteredAs(String registeredAs) {
            this.registeredAs = registeredAs;
        }
    }

    public String getNick() {
        return nick;
    }

    public String getLogin() {
        return login;
    }

    public String getHostname() {
        return hostname;
    }

    public String getRealname() {
        return realname;
    }

    public ImmutableList<String> getChannels() {
        return channels;
    }

    public String getServer() {
        return server;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public long getIdleSeconds() {
        return idleSeconds;
    }

    public long getSignOnTime() {
        return signOnTime;
    }

    public String getRegisteredAs() {
        return registeredAs;
    }
}

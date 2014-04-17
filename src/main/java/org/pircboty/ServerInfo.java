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

import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This is a giant info bean of various things about the server. This is
 * separate from the {@link PircBotY} class due to its length
 * <p/>
 * Most info thanks to <a href="www.irc.org/tech_docs/005.html">this great
 * website
 * </a> on what each one does
 *
 * @author
 */
public class ServerInfo {

    private final PircBotY bot;
    //004 information
    private String serverName;
    private String serverVersion;
    private String userModes;
    //005 information
    private final LinkedHashMap<String, String> isupportRaw = new LinkedHashMap<String, String>();
    private String prefixes;
    private String channelTypes;
    private String channelModes;
    private int maxModes;
    private int maxChannels;
    private String chanlimit;
    private int maxNickLength;
    private int maxBans;
    private ImmutableMap<String, Integer> maxList;
    private String network;
    private String exceptBans;
    private String exceptInvites;
    private String invites;
    private boolean wallOps;
    private boolean wallVoices;
    private String statusMessage;
    private String caseMapping;
    private String eList;
    private int topicLength;
    private int kickLength;
    private int channelLength;
    private String channelIDLength;
    private String standard;
    private int silence;
    private boolean RFC2812;
    private boolean penalty;
    private boolean forcedNickChanges;
    private boolean safeList;
    private int awayLength;
    private boolean noQuit;
    private boolean userIPExists;
    private boolean cPrivMsgExists;
    private boolean cNoticeExists;
    private int maxTargets;
    private boolean knockExists;
    private boolean vChannels;
    private int watchMax;
    private boolean whoX;
    private boolean callerID;
    private boolean accept;
    private String language;
    //Other information
    private String motd;
    private int highestConnections;
    private int highestClients;
    private int totalUsers;
    private int totalInvisibleUsers;
    private int totalServers;
    private int totalOperatorsOnline;
    private int totalUnknownConnections;
    private int totalChannelsFormed;
    private int serverUsers;
    private int connectedServers;

    public ServerInfo(PircBotY bot) {
        this.bot = bot;
    }

    protected void parse(int code, List<String> parsedLine) {
        //Pass off to speicific methods
        if (code == 004) {
            parse004(parsedLine);
        } else if (code == 005) {
            parse005(parsedLine);
        }
    }

    private void parse004(List<String> parsedLine) {
        //004 PircBotY pratchett.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
        serverName = parsedLine.get(1);
        serverVersion = parsedLine.get(2);
        userModes = parsedLine.get(3);
        channelModes = parsedLine.get(4);
    }

    private void parse005(List<String> parsedLine) {
        //REFERENCE: http://www.irc.org/tech_docs/005.html
        for (String curItem : parsedLine) {
            String[] itemParts = curItem.split("=", 2);
            String key = itemParts[0];
            String value = (itemParts.length == 2) ? itemParts[1] : "";
            isupportRaw.put(key, value);
            if (key.equalsIgnoreCase("PREFIX")) {
                prefixes = value;
            } else if (key.equalsIgnoreCase("CHANTYPES")) {
                channelTypes = value;
            } else if (key.equalsIgnoreCase("CHANMODES")) {
                channelModes = value;
            } else if (key.equalsIgnoreCase("MODES")) {
                maxModes = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("MAXCHANNELS")) {
                maxChannels = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("CHANLIMIT")) {
                chanlimit = value;
            } else if (key.equalsIgnoreCase("NICKLEN")) {
                maxNickLength = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("MAXBANS")) {
                maxBans = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("MAXLIST")) {
                StringTokenizer maxListTokens = new StringTokenizer(value, ":,");
                ImmutableMap.Builder<String, Integer> maxListBuilder = ImmutableMap.builder();
                while (maxListTokens.hasMoreTokens()) {
                    maxListBuilder.put(maxListTokens.nextToken(), Integer.parseInt(maxListTokens.nextToken()));
                }
                maxList = maxListBuilder.build();
            } else if (key.equalsIgnoreCase("NETWORK")) {
                network = value;
            } else if (key.equalsIgnoreCase("EXCEPTS")) {
                exceptBans = value;
            } else if (key.equalsIgnoreCase("INVEX")) {
                exceptInvites = value;
            } else if (key.equalsIgnoreCase("WALLCHOPS")) {
                wallOps = true;
            } else if (key.equalsIgnoreCase("WALLVOICES")) {
                wallVoices = true;
            } else if (key.equalsIgnoreCase("STATUSMSG")) {
                statusMessage = value;
            } else if (key.equalsIgnoreCase("CASEMAPPING")) {
                caseMapping = value;
            } else if (key.equalsIgnoreCase("ELIST")) {
                eList = value;
            } else if (key.equalsIgnoreCase("TOPICLEN")) {
                topicLength = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("KICKLEN")) {
                kickLength = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("CHANNELLEN")) {
                channelLength = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("CHIDLEN")) {
                channelIDLength = "!:" + Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("IDCHAN")) {
                channelIDLength = value;
            } else if (key.equalsIgnoreCase("STD")) {
                standard = value;
            } else if (key.equalsIgnoreCase("SILENCE")) {
                silence = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("RFC2812")) {
                RFC2812 = true;
            } else if (key.equalsIgnoreCase("PENALTY")) {
                penalty = true;
            } else if (key.equalsIgnoreCase("CPRIVMSG")) {
                cPrivMsgExists = true;
            } else if (key.equalsIgnoreCase("CNOTICE")) {
                cNoticeExists = true;
            } else if (key.equalsIgnoreCase("SAFELIST")) {
                safeList = true;
            } else if (key.equalsIgnoreCase("KNOCK")) {
                knockExists = true;
            } else if (key.equalsIgnoreCase("WHOX")) {
                whoX = true;
            } else if (key.equalsIgnoreCase("CALLERID") || key.equalsIgnoreCase("ACCEPT")) {
                callerID = true;
            } else if (key.equalsIgnoreCase("USERIP")) {
                userIPExists = true;
            } else if (key.equalsIgnoreCase("CNOTICE")) {
                cNoticeExists = true;
            }
        }
        //Freenode
        //005 PircBotY CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLMPQcgimnprstz CHANLIMIT=#:120 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=freenode KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
        //005 PircBotY CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=16 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
        //005 PircBotY EXTBAN=$,arx WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
        //Rizon
        //005 PircBotY CALLERID CASEMAPPING=rfc1459 DEAF=D KICKLEN=160 MODES=4 NICKLEN=30 TOPICLEN=390 PREFIX=(qaohv)~&@%+ STATUSMSG=~&@%+ NETWORK=Rizon MAXLIST=beI:100 TARGMAX=ACCEPT:,KICK:1,LIST:1,NAMES:1,NOTICE:4,PRIVMSG:4,WHOIS:1 CHANTYPES=# :are supported by this server
        //005 PircBotY CHANLIMIT=#:75 CHANNELLEN=50 CHANMODES=beI,k,l,BCMNORScimnpstz AWAYLEN=160 ELIST=CMNTU SAFELIST KNOCK NAMESX UHNAMES FNC EXCEPTS=e INVEX=I :are supported by this server
    }

    /**
     * Get all supported server options as a map. Be careful about calling this
     * very early in the connection phase as we might not of received all the
     * 005 lines yet
     *
     * @return An <i>immutable copy</i> of the current supported options
     */
    public ImmutableMap<String, String> getIsupportRaw() {
        return ImmutableMap.copyOf(isupportRaw);
    }

    public PircBotY getBot() {
        return bot;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public String getUserModes() {
        return userModes;
    }

    public String getPrefixes() {
        return prefixes;
    }

    public String getChannelTypes() {
        return channelTypes;
    }

    public String getChannelModes() {
        return channelModes;
    }

    public int getMaxModes() {
        return maxModes;
    }

    public int getMaxChannels() {
        return maxChannels;
    }

    public String getChanlimit() {
        return chanlimit;
    }

    public int getMaxNickLength() {
        return maxNickLength;
    }

    public int getMaxBans() {
        return maxBans;
    }

    public ImmutableMap<String, Integer> getMaxList() {
        return maxList;
    }

    public String getNetwork() {
        return network;
    }

    public String getExceptBans() {
        return exceptBans;
    }

    public String getExceptInvites() {
        return exceptInvites;
    }

    public String getInvites() {
        return invites;
    }

    public boolean isWallOps() {
        return wallOps;
    }

    public boolean isWallVoices() {
        return wallVoices;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getCaseMapping() {
        return caseMapping;
    }

    public String geteList() {
        return eList;
    }

    public int getTopicLength() {
        return topicLength;
    }

    public int getKickLength() {
        return kickLength;
    }

    public int getChannelLength() {
        return channelLength;
    }

    public String getChannelIDLength() {
        return channelIDLength;
    }

    public String getStandard() {
        return standard;
    }

    public int getSilence() {
        return silence;
    }

    public boolean isRFC2812() {
        return RFC2812;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public boolean isForcedNickChanges() {
        return forcedNickChanges;
    }

    public boolean isSafeList() {
        return safeList;
    }

    public int getAwayLength() {
        return awayLength;
    }

    public boolean isNoQuit() {
        return noQuit;
    }

    public boolean isUserIPExists() {
        return userIPExists;
    }

    public boolean iscPrivMsgExists() {
        return cPrivMsgExists;
    }

    public boolean iscNoticeExists() {
        return cNoticeExists;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public boolean isKnockExists() {
        return knockExists;
    }

    public boolean isvChannels() {
        return vChannels;
    }

    public int getWatchMax() {
        return watchMax;
    }

    public boolean isWhoX() {
        return whoX;
    }

    public boolean isCallerID() {
        return callerID;
    }

    public boolean isAccept() {
        return accept;
    }

    public String getLanguage() {
        return language;
    }

    public String getMotd() {
        return motd;
    }

    public int getHighestConnections() {
        return highestConnections;
    }

    public int getHighestClients() {
        return highestClients;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getTotalInvisibleUsers() {
        return totalInvisibleUsers;
    }

    public int getTotalServers() {
        return totalServers;
    }

    public int getTotalOperatorsOnline() {
        return totalOperatorsOnline;
    }

    public int getTotalUnknownConnections() {
        return totalUnknownConnections;
    }

    public int getTotalChannelsFormed() {
        return totalChannelsFormed;
    }

    public int getServerUsers() {
        return serverUsers;
    }

    public int getConnectedServers() {
        return connectedServers;
    }

    protected void setServerName(String serverName) {
        this.serverName = serverName;
    }

    protected void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    protected void setUserModes(String userModes) {
        this.userModes = userModes;
    }

    protected void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }

    protected void setChannelTypes(String channelTypes) {
        this.channelTypes = channelTypes;
    }

    protected void setChannelModes(String channelModes) {
        this.channelModes = channelModes;
    }

    protected void setMaxModes(int maxModes) {
        this.maxModes = maxModes;
    }

    protected void setMaxChannels(int maxChannels) {
        this.maxChannels = maxChannels;
    }

    protected void setChanlimit(String chanlimit) {
        this.chanlimit = chanlimit;
    }

    protected void setMaxNickLength(int maxNickLength) {
        this.maxNickLength = maxNickLength;
    }

    protected void setMaxBans(int maxBans) {
        this.maxBans = maxBans;
    }

    protected void setMaxList(ImmutableMap<String, Integer> maxList) {
        this.maxList = maxList;
    }

    protected void setNetwork(String network) {
        this.network = network;
    }

    protected void setExceptBans(String exceptBans) {
        this.exceptBans = exceptBans;
    }

    protected void setExceptInvites(String exceptInvites) {
        this.exceptInvites = exceptInvites;
    }

    protected void setInvites(String invites) {
        this.invites = invites;
    }

    protected void setWallOps(boolean wallOps) {
        this.wallOps = wallOps;
    }

    protected void setWallVoices(boolean wallVoices) {
        this.wallVoices = wallVoices;
    }

    protected void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    protected void setCaseMapping(String caseMapping) {
        this.caseMapping = caseMapping;
    }

    protected void seteList(String eList) {
        this.eList = eList;
    }

    protected void setTopicLength(int topicLength) {
        this.topicLength = topicLength;
    }

    protected void setKickLength(int kickLength) {
        this.kickLength = kickLength;
    }

    protected void setChannelLength(int channelLength) {
        this.channelLength = channelLength;
    }

    protected void setChannelIDLength(String channelIDLength) {
        this.channelIDLength = channelIDLength;
    }

    protected void setStandard(String standard) {
        this.standard = standard;
    }

    protected void setSilence(int silence) {
        this.silence = silence;
    }

    protected void setRFC2812(boolean RFC2812) {
        this.RFC2812 = RFC2812;
    }

    protected void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }

    protected void setForcedNickChanges(boolean forcedNickChanges) {
        this.forcedNickChanges = forcedNickChanges;
    }

    protected void setSafeList(boolean safeList) {
        this.safeList = safeList;
    }

    protected void setAwayLength(int awayLength) {
        this.awayLength = awayLength;
    }

    protected void setNoQuit(boolean noQuit) {
        this.noQuit = noQuit;
    }

    protected void setUserIPExists(boolean userIPExists) {
        this.userIPExists = userIPExists;
    }

    protected void setcPrivMsgExists(boolean cPrivMsgExists) {
        this.cPrivMsgExists = cPrivMsgExists;
    }

    protected void setcNoticeExists(boolean cNoticeExists) {
        this.cNoticeExists = cNoticeExists;
    }

    protected void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    protected void setKnockExists(boolean knockExists) {
        this.knockExists = knockExists;
    }

    protected void setvChannels(boolean vChannels) {
        this.vChannels = vChannels;
    }

    protected void setWatchMax(int watchMax) {
        this.watchMax = watchMax;
    }

    protected void setWhoX(boolean whoX) {
        this.whoX = whoX;
    }

    protected void setCallerID(boolean callerID) {
        this.callerID = callerID;
    }

    protected void setAccept(boolean accept) {
        this.accept = accept;
    }

    protected void setLanguage(String language) {
        this.language = language;
    }

    protected void setMotd(String motd) {
        this.motd = motd;
    }

    protected void setHighestConnections(int highestConnections) {
        this.highestConnections = highestConnections;
    }

    protected void setHighestClients(int highestClients) {
        this.highestClients = highestClients;
    }

    protected void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    protected void setTotalInvisibleUsers(int totalInvisibleUsers) {
        this.totalInvisibleUsers = totalInvisibleUsers;
    }

    protected void setTotalServers(int totalServers) {
        this.totalServers = totalServers;
    }

    protected void setTotalOperatorsOnline(int totalOperatorsOnline) {
        this.totalOperatorsOnline = totalOperatorsOnline;
    }

    protected void setTotalUnknownConnections(int totalUnknownConnections) {
        this.totalUnknownConnections = totalUnknownConnections;
    }

    protected void setTotalChannelsFormed(int totalChannelsFormed) {
        this.totalChannelsFormed = totalChannelsFormed;
    }

    protected void setServerUsers(int serverUsers) {
        this.serverUsers = serverUsers;
    }

    protected void setConnectedServers(int connectedServers) {
        this.connectedServers = connectedServers;
    }
}

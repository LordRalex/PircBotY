package net.ae97.pircboty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.ae97.pircboty.api.WaitForQueue;
import net.ae97.pircboty.api.events.WhoisEvent;
import net.ae97.pircboty.output.OutputUser;
import net.ae97.pircboty.snapshot.UserSnapshot;
import net.ae97.pokebot.permissions.Permissible;
import net.ae97.pokebot.permissions.Permission;
import org.apache.commons.lang3.concurrent.AtomicSafeInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;

public class User implements Comparable<User>, Permissible {

    private final PircBotY bot;
    private final UserChannelDao<PircBotY, User, Channel> dao;
    private final UUID userId = UUID.randomUUID();
    private final AtomicSafeInitializer<OutputUser> output = new AtomicSafeInitializer<OutputUser>() {
        @Override
        protected OutputUser initialize() {
            return bot.getConfiguration().getBotFactory().createOutputUser(bot, User.this);
        }
    };
    private final Map<String, Set<Permission>> permissions = new HashMap<>();
    private String nick;
    private String realName = "";
    private String login = "";
    private String hostmask = "";
    private String awayMessage = null;
    private boolean ircop = false;
    private String server = "";
    private int hops = 0;
    private String accountName = null;

    protected User(PircBotY bot, UserChannelDao<PircBotY, User, Channel> dao, String nick) {
        this.bot = bot;
        this.dao = dao;
        this.nick = nick;
    }

    public OutputUser send() {
        try {
            return output.get();
        } catch (ConcurrentException ex) {
            throw new RuntimeException("Could not generate OutputChannel for " + getNick(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isVerified() {
        if (accountName != null) {
            try {
                bot.sendRaw().rawLine("WHOIS " + getNick() + " " + getNick());
                WaitForQueue waitForQueue = new WaitForQueue(bot);
                while (true) {
                    WhoisEvent event = waitForQueue.waitFor(WhoisEvent.class);
                    if (!event.getNick().equals(nick)) {
                        continue;
                    }
                    waitForQueue.close();
                    accountName = event.getRegisteredAs();
                    return accountName != null && !accountName.isEmpty();
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Couldn't finish querying user for verified status", ex);
            }
        } else {
            return true;
        }
    }

    public String getAccountName() {
        if (accountName == null) {
            isVerified();
        }
        return accountName;
    }

    public UserSnapshot createSnapshot() {
        return new UserSnapshot(this);
    }

    public <C extends Channel> Set<UserLevel> getUserLevels(C channel) {
        return getDao().getLevels(channel, this);
    }

    public Set<Channel> getChannels() {
        return getDao().getChannels(this);
    }

    public Set<Channel> getChannelsOpIn() {
        return getDao().getChannels(this, UserLevel.OP);
    }

    public Set<Channel> getChannelsVoiceIn() {
        return getDao().getChannels(this, UserLevel.VOICE);
    }

    public Set<Channel> getChannelsOwnerIn() {
        return getDao().getChannels(this, UserLevel.OWNER);
    }

    public Set<Channel> getChannelsHalfOpIn() {
        return getDao().getChannels(this, UserLevel.HALFOP);
    }

    public Set<Channel> getChannelsSuperOpIn() {
        return getDao().getChannels(this, UserLevel.SUPEROP);
    }

    @Override
    public int compareTo(User other) {
        return getNick().compareToIgnoreCase(other.getNick());
    }

    public String getServer() {
        return server;
    }

    public int getHops() {
        return hops;
    }

    public boolean isAway() {
        return awayMessage != null;
    }

    public PircBotY getBot() {
        return bot;
    }

    public UserChannelDao<PircBotY, User, Channel> getDao() {
        return dao;
    }

    public UUID getUserId() {
        return userId;
    }

    public AtomicSafeInitializer<OutputUser> getOutput() {
        return output;
    }

    public String getNick() {
        return nick;
    }

    public String getRealName() {
        return realName;
    }

    public String getLogin() {
        return login;
    }

    public String getHostmask() {
        return hostmask;
    }

    public String getAwayMessage() {
        return awayMessage;
    }

    public boolean isIrcop() {
        return ircop;
    }

    protected void setNick(String nick) {
        this.nick = nick;
    }

    protected void setRealName(String realName) {
        this.realName = realName;
    }

    protected void setLogin(String login) {
        this.login = login;
    }

    protected void setHostmask(String hostmask) {
        this.hostmask = hostmask;
    }

    protected void setAwayMessage(String awayMessage) {
        this.awayMessage = awayMessage;
    }

    protected void setIrcop(boolean ircop) {
        this.ircop = ircop;
    }

    protected void setServer(String server) {
        this.server = server;
    }

    protected void setHops(int hops) {
        this.hops = hops;
    }

    @Override
    public boolean hasPermission(String channel, String perm) {
        Set<Permission> perms = permissions.get(channel.toLowerCase());
        return perms != null && perms.contains(perm);
    }

    @Override
    public void addPermission(String channel, String perm) {

    }

    @Override
    public void removePermission(String channel, String perm) {

    }

    @Override
    public Map<String, Set<Permission>> getPermissions() {
        return permissions;
    }
}

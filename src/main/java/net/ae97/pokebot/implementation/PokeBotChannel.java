package net.ae97.pokebot.implementation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.permissions.Permission;

public class PokeBotChannel extends Channel {

    private final net.ae97.pircboty.Channel PircBotYChannel;
    private final PircBotY bot;
    protected final Map<String, Set<Permission>> permMap = new HashMap<>();

    public PokeBotChannel(PircBotY ab, String chan) {
        this(ab, ab.getUserChannelDao().getChannel(chan));
    }

    public PokeBotChannel(PircBotY aB, net.ae97.pircboty.Channel channel) {
        bot = aB;
        PircBotYChannel = channel;
    }

    @Override
    public void sendMessage(String... messages) {
        for (String message : messages) {
            bot.sendIRC().message(PircBotYChannel.getName(), message);
        }
    }

    @Override
    public void sendNotice(String... messages) {
        for (String message : messages) {
            bot.sendIRC().notice(PircBotYChannel.getName(), message);
        }
    }

    @Override
    public void sendAction(String... messages) {
        for (String message : messages) {
            sendAction(message);
        }
    }

    @Override
    public boolean isSecret() {
        return PircBotYChannel.isSecret();
    }

    @Override
    public String[] getOps() {
        User[] users = PircBotYChannel.getOps().toArray(new User[0]);
        String[] names = new String[users.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = users[i].getNick();
        }
        return names;
    }

    @Override
    public String[] getVoiced() {
        User[] users = PircBotYChannel.getVoices().toArray(new User[0]);
        String[] names = new String[users.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = users[i].getNick();
        }
        return names;
    }

    @Override
    public boolean hasOp(String name) {
        return PircBotYChannel.isOp(bot.getUserChannelDao().getUser(name));
    }

    @Override
    public boolean hasVoice(String name) {
        return PircBotYChannel.hasVoice(bot.getUserChannelDao().getUser(name));
    }

    @Override
    public String getName() {
        return PircBotYChannel.getName();
    }

    @Override
    public String[] getUserList() {
        Set<User> users = PircBotYChannel.getUsers();
        LinkedList<String> names = new LinkedList<>();
        for (User user : users) {
            names.add(user.getNick());
        }
        return names.toArray(new String[names.size()]);
    }

    @Override
    public boolean hasPermission(String channel, String perm) {
        Set<Permission> set = permMap.get(channel == null ? null : channel.toLowerCase());
        if (set != null) {
            for (Permission p : set.toArray(new Permission[set.size()])) {
                if (p.getName().equalsIgnoreCase(perm)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addPermission(String channel, String perm) {
        removePermission(channel, perm);
        Set<Permission> set = permMap.get(channel == null ? null : channel.toLowerCase());
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(new Permission(perm));
        permMap.put(channel == null ? null : channel.toLowerCase(), set);
    }

    @Override
    public void removePermission(String channel, String perm) {
        Set<Permission> set = permMap.get(channel == null ? null : channel.toLowerCase());
        if (set == null) {
            return;
        }
        for (Permission p : set.toArray(new Permission[set.size()])) {
            if (p.getName().equalsIgnoreCase(perm)) {
                set.remove(p);
            }
        }
    }

    @Override
    public Map<String, Set<Permission>> getPermissions() {
        return permMap;
    }

    @Override
    public void setMode(char mode, boolean newState) {
        String newMode = (newState ? "+" : "-") + mode;
        bot.sendIRC().mode(PircBotYChannel.getName(), newMode);
    }

    @Override
    public void kickUser(String name, String reason) {
        if (reason != null) {
            PircBotYChannel.send().kick(bot.getUserChannelDao().getUser(name), reason);
        } else {
            PircBotYChannel.send().kick(bot.getUserChannelDao().getUser(name));
        }
    }

    @Override
    public void ban(String mask) {
        PircBotYChannel.send().ban(mask);
    }

    @Override
    public void unban(String mask) {
        PircBotYChannel.send().unBan(mask);
    }

    @Override
    public void opUser(String user) {
        PircBotYChannel.send().op(bot.getUserChannelDao().getUser(user));
    }

    @Override
    public void deopUser(String user) {
        PircBotYChannel.send().deOp(bot.getUserChannelDao().getUser(user));
    }

    @Override
    public void voiceUser(String user) {
        PircBotYChannel.send().voice(bot.getUserChannelDao().getUser(user));
    }

    @Override
    public void devoiceUser(String user) {
        PircBotYChannel.send().deVoice(bot.getUserChannelDao().getUser(user));
    }

    @Override
    public void quiet(String mask) {
    }

    @Override
    public void unquiet(String mask) {
    }
}

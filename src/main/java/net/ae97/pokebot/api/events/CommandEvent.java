package net.ae97.pokebot.api.events;

import net.ae97.pokebot.PokeBot;
import net.ae97.pokebot.api.channels.Channel;
import net.ae97.pokebot.api.recipients.MessageRecipient;
import net.ae97.pokebot.api.users.User;
import net.ae97.pokebot.eventhandler.EventHandler;

public class CommandEvent implements UserEvent, ChannelEvent, CancellableEvent, ReplyableEvent {

    private final String command;
    private final User sender;
    private final Channel channel;
    private final String[] args;
    private final long timestamp = System.currentTimeMillis();
    private boolean isCancelled = false;

    public CommandEvent(net.ae97.pircboty.hooks.events.MessageEvent event) {
        String[] temp = event.getMessage().split(" ");
        String commandTemp = temp[0].toLowerCase();
        for (String cmd : EventHandler.getCommandPrefixes()) {
            if (commandTemp.startsWith(cmd)) {
                commandTemp = commandTemp.substring(cmd.length());
                break;
            }
        }
        command = commandTemp;
        channel = PokeBot.getChannel(event.getChannel().getName());
        sender = PokeBot.getUser(event.getUser().getNick());
        args = new String[temp.length - 1];
        if (temp.length >= 2) {
            System.arraycopy(temp, 1, args, 0, args.length);
        }
    }

    public CommandEvent(net.ae97.pircboty.hooks.events.PrivateMessageEvent event) {
        String[] temp = event.getMessage().split(" ");
        command = temp[0].substring(1).toLowerCase();
        sender = PokeBot.getUser(event.getUser().getNick());
        channel = null;
        args = new String[temp.length - 1];
        if (temp.length >= 2) {
            System.arraycopy(temp, 1, args, 0, args.length);
        }
    }

    public CommandEvent(net.ae97.pircboty.hooks.events.NoticeEvent event) {
        String[] temp = event.getMessage().split(" ");
        command = temp[0].substring(1).toLowerCase();
        sender = PokeBot.getUser(event.getUser().getNick());
        channel = null;
        args = new String[temp.length - 1];
        if (temp.length >= 2) {
            System.arraycopy(temp, 1, args, 0, args.length);
        }
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public User getUser() {
        return sender;
    }

    @Override
    public void setCancelled(boolean state) {
        isCancelled = state;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void reply(String... messages) {
        MessageRecipient rec = channel == null ? sender : channel;
        if (rec == null) {
            return;
        }
        rec.sendMessage(messages);
    }
}

package net.ae97.pircboty.hooks.events;

import com.google.common.collect.ImmutableList;
import net.ae97.pircboty.ChannelListEntry;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;

public class ChannelInfoEvent extends Event {

    private final ImmutableList<ChannelListEntry> list;

    public ChannelInfoEvent(PircBotY bot, ImmutableList<ChannelListEntry> list) {
        super(bot);
        this.list = list;
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public ImmutableList<ChannelListEntry> getList() {
        return list;
    }
}

package net.ae97.pircboty.api.events;

import java.util.List;
import net.ae97.pircboty.ChannelListEntry;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;

public class ChannelInfoEvent extends Event {

    private final List<ChannelListEntry> list;

    public ChannelInfoEvent(PircBotY bot, List<ChannelListEntry> list) {
        super(bot);
        this.list = list;
    }

    @Override
    public void respond(String response) {
        getBot().sendRaw().rawLine(response);
    }

    public List<ChannelListEntry> getList() {
        return list;
    }
}

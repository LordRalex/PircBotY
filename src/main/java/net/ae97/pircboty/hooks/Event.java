package net.ae97.pircboty.hooks;

import com.google.common.collect.ComparisonChain;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.managers.ListenerManager;
import net.ae97.pircboty.hooks.types.GenericEvent;

public abstract class Event implements GenericEvent {

    private final long timestamp;
    private final PircBotY bot;
    private final long id;

    public Event(PircBotY bot) {
        this(bot, bot.getConfiguration().getListenerManager());
    }

    public Event(ListenerManager listenerManager) {
        this(null, listenerManager);
    }

    public Event(PircBotY bot, ListenerManager listenerManager) {
        this.timestamp = System.currentTimeMillis();
        this.bot = bot;
        this.id = listenerManager.incrementCurrentId();
    }

    @Override
    public PircBotY getBot() {
        return bot;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public long getId() {
        return id;
    }

    @Override
    public int compareTo(Event other) {
        ComparisonChain comparison = ComparisonChain.start()
                .compare(getTimestamp(), other.getTimestamp())
                .compare(getId(), other.getId());
        if (bot != null && other.getBot() != null) {
            comparison.compare(bot.getBotId(), other.getBot().getBotId());
        }
        return comparison.result();
    }
}

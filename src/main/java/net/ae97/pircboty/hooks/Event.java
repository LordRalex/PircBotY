package net.ae97.pircboty.hooks;

import com.google.common.collect.ComparisonChain;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.managers.ListenerManager;
import net.ae97.pircboty.hooks.types.GenericEvent;

public abstract class Event<T extends PircBotY> implements GenericEvent<T> {

    private final long timestamp;
    private final T bot;
    private final long id;

    public Event(T bot) {
        this(bot, bot.getConfiguration().getListenerManager());
    }

    public Event(ListenerManager<? extends PircBotY> listenerManager) {
        this(null, listenerManager);
    }

    public Event(T bot, ListenerManager<? extends PircBotY> listenerManager) {
        this.timestamp = System.currentTimeMillis();
        this.bot = bot;
        this.id = listenerManager.incrementCurrentId();
    }

    @Override
    public T getBot() {
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
    public int compareTo(Event<T> other) {
        ComparisonChain comparison = ComparisonChain.start()
                .compare(getTimestamp(), other.getTimestamp())
                .compare(getId(), other.getId());
        if (bot != null && other.getBot() != null) {
            comparison.compare(bot.getBotId(), other.getBot().getBotId());
        }
        return comparison.result();
    }
}

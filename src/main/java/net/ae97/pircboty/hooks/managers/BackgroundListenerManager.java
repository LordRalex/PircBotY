package net.ae97.pircboty.hooks.managers;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.Listener;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class BackgroundListenerManager<B extends PircBotY> extends ThreadedListenerManager<B> {

    private final Map<Listener<B>, ExecutorService> backgroundListeners = new HashMap<Listener<B>, ExecutorService>();
    private final AtomicInteger backgroundCount = new AtomicInteger();

    public boolean addListener(Listener<B> listener, boolean isBackground) {
        if (!isBackground) {
            return super.addListener(listener);
        }
        BasicThreadFactory factory = new BasicThreadFactory.Builder()
                .namingPattern("backgroundPool" + getManagerNumber() + "-backgroundThread" + backgroundCount.getAndIncrement() + "-%d")
                .daemon(true)
                .build();
        backgroundListeners.put(listener, Executors.newSingleThreadExecutor(factory));
        return true;
    }

    @Override
    public void dispatchEvent(Event<B> event) {
        super.dispatchEvent(event);
        for (Map.Entry<Listener<B>, ExecutorService> curEntry : backgroundListeners.entrySet()) {
            submitEvent(curEntry.getValue(), curEntry.getKey(), event);
        }
    }

    @Override
    public ImmutableSet<Listener<B>> getListeners() {
        return ImmutableSet.<Listener<B>>builder()
                .addAll(getListeners())
                .addAll(backgroundListeners.keySet())
                .build();
    }

    @Override
    public boolean removeListener(Listener<B> listener) {
        if (backgroundListeners.containsKey(listener)) {
            return backgroundListeners.remove(listener) != null;
        } else {
            return super.removeListener(listener);
        }
    }
}

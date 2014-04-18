package net.ae97.pircboty.hooks;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import net.ae97.pircboty.PircBotY;

public class WaitForQueue implements Closeable {

    private final PircBotY bot;
    private final LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private final WaitForQueueListener listener;

    public WaitForQueue(PircBotY bot) {
        this.bot = bot;
        listener = new WaitForQueueListener();
        bot.getConfiguration().getListenerManager().addListener(listener);
    }

    public <E extends Event> E waitFor(Class<E> eventClass) throws InterruptedException {
        List<Class<? extends Event>> eventList = new ArrayList<>();
        eventList.add(eventClass);
        return (E) waitFor(eventList);
    }

    public Event waitFor(List<Class<? extends Event>> eventClasses) throws InterruptedException {
        return waitFor(eventClasses, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public Event waitFor(List<Class<? extends Event>> eventClasses, long timeout, TimeUnit unit) throws InterruptedException {
        while (true) {
            Event curEvent = eventQueue.poll(timeout, unit);
            for (Class<? extends Event> curEventClass : eventClasses) {
                if (curEventClass.isInstance(curEvent)) {
                    return curEvent;
                }
            }
        }
    }

    @Override
    public void close() {
        bot.getConfiguration().getListenerManager().removeListener(listener);
        eventQueue.clear();
    }

    private class WaitForQueueListener implements Listener {

        @Override
        public void onEvent(Event event) throws Exception {
            eventQueue.add(event);
        }
    }
}

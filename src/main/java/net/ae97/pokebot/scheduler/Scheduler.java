package net.ae97.pokebot.scheduler;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<Integer, ScheduledFuture<?>> index = new ConcurrentHashMap<>();
    private volatile Integer id = 0;

    public Scheduler() {
    }

    public int scheduleTask(Runnable task, int delay, TimeUnit unit) {
        ScheduledFuture<?> future = executorService.schedule(task, delay, unit);
        int currentID;
        synchronized (id) {
            id++;
            currentID = id.intValue();
        }
        index.put(currentID, future);
        return currentID;
    }

    public int scheduleTask(Callable task, int delay, TimeUnit unit) {
        ScheduledFuture<?> future = executorService.schedule(task, delay, unit);
        int currentID;
        synchronized (id) {
            id++;
            currentID = id.intValue();
        }
        index.put(currentID, future);
        return currentID;
    }

    public boolean isRunning(int id) {
        ScheduledFuture<?> future = index.get(id);
        return future == null ? true : future.getDelay(TimeUnit.NANOSECONDS) <= 0;
    }

    public boolean isDone(int id) {
        ScheduledFuture<?> future = index.get(id);
        return future == null ? true : future.isDone();
    }

    public boolean isCancelled(int id) {
        ScheduledFuture<?> future = index.get(id);
        return future == null ? true : future.isCancelled();
    }

    public void cancelTask(int id) {
        ScheduledFuture<?> future = index.remove(id);
        if (future != null) {
            future.cancel(true);
        }
    }
}

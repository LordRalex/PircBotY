package net.ae97.pokebot.scheduler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final Map<UUID, ScheduledFuture<?>> index = new ConcurrentHashMap<>();

    public Scheduler() {
    }

    public UUID scheduleTask(Runnable task, int delay, TimeUnit unit) {
        ScheduledFuture<?> future = executorService.schedule(task, delay, unit);
        UUID currentID = UUID.randomUUID();
        index.put(currentID, future);
        return currentID;
    }

    public UUID scheduleTask(Callable<?> task, int delay, TimeUnit unit) {
        ScheduledFuture<?> future = executorService.schedule(task, delay, unit);
        UUID currentID = UUID.randomUUID();
        index.put(currentID, future);
        return currentID;
    }

    public boolean isRunning(UUID id) {
        ScheduledFuture<?> future = index.get(id);
        return future == null || future.getDelay(TimeUnit.NANOSECONDS) <= 0;
    }

    public boolean isDone(UUID id) {
        ScheduledFuture<?> future = index.get(id);
        return future == null || future.isDone();
    }

    public boolean isCancelled(UUID id) {
        ScheduledFuture<?> future = index.get(id);
        return future == null || future.isCancelled();
    }

    public void cancelTask(UUID id) {
        ScheduledFuture<?> future = index.remove(id);
        if (future != null) {
            future.cancel(true);
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}

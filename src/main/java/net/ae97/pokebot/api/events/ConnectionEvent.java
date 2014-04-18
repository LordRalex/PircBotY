package net.ae97.pokebot.api.events;

public class ConnectionEvent implements Event {

    private final long timestamp;

    public ConnectionEvent() {
        timestamp = System.currentTimeMillis();
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}

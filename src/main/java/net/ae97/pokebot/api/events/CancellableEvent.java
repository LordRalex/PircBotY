package net.ae97.pokebot.api.events;

public interface CancellableEvent {

    public void setCancelled(boolean newstate);

    public boolean isCancelled();
}

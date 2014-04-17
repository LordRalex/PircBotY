package net.ae97.pircboty.hooks.managers;

import com.google.common.collect.ImmutableSet;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.Event;
import net.ae97.pircboty.hooks.Listener;

public interface ListenerManager<B extends PircBotY> {

    public void dispatchEvent(Event<B> event);

    public boolean addListener(Listener<B> listener);

    public boolean removeListener(Listener<B> listener);

    public boolean listenerExists(Listener<B> listener);

    public ImmutableSet<Listener<B>> getListeners();

    public void setCurrentId(long currentId);

    public long getCurrentId();

    public long incrementCurrentId();

    public void shutdown(B bot);
}

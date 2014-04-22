package net.ae97.pircboty.managers;

import java.util.Set;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.api.Listener;

public interface ListenerManager {

    public void dispatchEvent(Event event);

    public boolean addListener(Listener listener);

    public boolean removeListener(Listener listener);

    public boolean listenerExists(Listener listener);

    public Set<Listener> getListeners();

    public void setCurrentId(long currentId);

    public long getCurrentId();

    public long incrementCurrentId();

    public void shutdown(PircBotY bot);
}

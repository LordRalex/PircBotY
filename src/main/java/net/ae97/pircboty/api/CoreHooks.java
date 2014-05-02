package net.ae97.pircboty.api;

import net.ae97.pircboty.api.events.ServerPingEvent;

public class CoreHooks extends ListenerAdapter {

    @Override
    public void onServerPing(ServerPingEvent event) {
        event.getBot().sendRaw().rawLine("PONG " + event.getResponse());
    }
}

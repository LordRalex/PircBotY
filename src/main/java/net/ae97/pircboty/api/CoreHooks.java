package net.ae97.pircboty.api;

import java.util.Date;
import net.ae97.pircboty.api.events.FingerEvent;
import net.ae97.pircboty.api.events.PingEvent;
import net.ae97.pircboty.api.events.ServerPingEvent;
import net.ae97.pircboty.api.events.TimeEvent;
import net.ae97.pircboty.api.events.VersionEvent;

public class CoreHooks extends ListenerAdapter {

    @Override
    public void onFinger(FingerEvent event) {
        event.getUser().send().ctcpResponse("FINGER " + event.getBot().getConfiguration().getFinger());
    }

    @Override
    public void onPing(PingEvent event) {
        event.getUser().send().ctcpResponse("PING " + event.getPingValue());
    }

    @Override
    public void onServerPing(ServerPingEvent event) {
        event.getBot().sendRaw().rawLine("PONG " + event.getResponse());
    }

    @Override
    public void onTime(TimeEvent event) {
        event.getUser().send().ctcpResponse("TIME " + new Date().toString());
    }

    @Override
    public void onVersion(VersionEvent event) {
        event.getUser().send().ctcpResponse("VERSION " + event.getBot().getConfiguration().getVersion());
    }
}

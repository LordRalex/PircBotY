package net.ae97.pircboty.hooks;

import java.util.Date;
import net.ae97.pircboty.hooks.events.FingerEvent;
import net.ae97.pircboty.hooks.events.PingEvent;
import net.ae97.pircboty.hooks.events.ServerPingEvent;
import net.ae97.pircboty.hooks.events.TimeEvent;
import net.ae97.pircboty.hooks.events.VersionEvent;

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

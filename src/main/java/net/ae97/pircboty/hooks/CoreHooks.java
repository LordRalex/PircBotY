package net.ae97.pircboty.hooks;

import java.util.Date;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.hooks.events.FingerEvent;
import net.ae97.pircboty.hooks.events.PingEvent;
import net.ae97.pircboty.hooks.events.ServerPingEvent;
import net.ae97.pircboty.hooks.events.TimeEvent;
import net.ae97.pircboty.hooks.events.VersionEvent;

public class CoreHooks<B extends PircBotY> extends ListenerAdapter<B> {

    @Override
    public void onFinger(FingerEvent<B> event) {
        event.getUser().send().ctcpResponse("FINGER " + event.getBot().getConfiguration().getFinger());
    }

    @Override
    public void onPing(PingEvent<B> event) {
        event.getUser().send().ctcpResponse("PING " + event.getPingValue());
    }

    @Override
    public void onServerPing(ServerPingEvent<B> event) {
        event.getBot().sendRaw().rawLine("PONG " + event.getResponse());
    }

    @Override
    public void onTime(TimeEvent<B> event) {
        event.getUser().send().ctcpResponse("TIME " + new Date().toString());
    }

    @Override
    public void onVersion(VersionEvent<B> event) {
        event.getUser().send().ctcpResponse("VERSION " + event.getBot().getConfiguration().getVersion());
    }
}

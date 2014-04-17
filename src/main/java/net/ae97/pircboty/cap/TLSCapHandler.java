package net.ae97.pircboty.cap;

import com.google.common.collect.ImmutableList;
import javax.net.ssl.SSLSocketFactory;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.exception.CAPException;

public class TLSCapHandler extends EnableCapHandler {

    protected SSLSocketFactory sslSocketFactory;

    public TLSCapHandler() {
        super("tls");
        this.sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    public TLSCapHandler(SSLSocketFactory sslSocketFactory, boolean ignoreFail) {
        super("tls", ignoreFail);
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public boolean handleACK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        if (capabilities.contains("tls")) {
            bot.sendRaw().rawLineNow("STARTTLS");
        }
        return false;
    }

    @Override
    public boolean handleUnknown(PircBotY bot, String rawLine) {
        return rawLine.contains(" 670 ");
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }
}

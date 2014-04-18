package net.ae97.pircboty.cap;

import com.google.common.collect.ImmutableList;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.exception.CAPException;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

public class SASLCapHandler implements CapHandler {

    private final String username;
    private final String password;
    private final boolean ignoreFail;

    public SASLCapHandler(String username, String password) {
        this.username = username;
        this.password = password;
        this.ignoreFail = false;
    }

    @Override
    public boolean handleLS(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        if (capabilities.contains("sasl")) {
            bot.sendCAP().request("sasl");
        } else {
            throw new CAPException(CAPException.Reason.UnsupportedCapability, "SASL");
        }
        return false;
    }

    @Override
    public boolean handleACK(PircBotY bot, ImmutableList<String> capabilities) {
        if (capabilities.contains("sasl")) {
            bot.sendRaw().rawLineNow("AUTHENTICATE PLAIN");
        }
        return false;
    }

    @Override
    public boolean handleUnknown(PircBotY bot, String rawLine) throws CAPException {
        if (rawLine.equals("AUTHENTICATE +")) {
            String encodedAuth = Base64.encodeBase64String((username + '\0' + username + '\0' + password).getBytes(Charsets.UTF_8));
            bot.sendRaw().rawLineNow("AUTHENTICATE " + encodedAuth);
        }
        String[] parsedLine = rawLine.split(" ", 4);
        if (parsedLine.length >= 1) {
            switch (parsedLine[1]) {
                case "904":
                case "905":
                    bot.getEnabledCapabilities().remove("sasl");
                    if (!ignoreFail) {
                        throw new CAPException(CAPException.Reason.SASLFailed, "SASL Authentication failed with message: " + parsedLine[3].substring(1));
                    }
                    return true;
                case "900":
                case "903":
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleNAK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        if (!ignoreFail && capabilities.contains("sasl")) {
            bot.getEnabledCapabilities().remove("sasl");
            throw new CAPException(CAPException.Reason.UnsupportedCapability, "SASL");
        }
        return false;
    }
}

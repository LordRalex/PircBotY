package net.ae97.pircboty.cap;

import com.google.common.collect.ImmutableList;
import java.util.logging.Level;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.exception.CAPException;

public class EnableCapHandler implements CapHandler {

    private final String cap;
    private final boolean ignoreFail;

    public EnableCapHandler(String cap) {
        this.cap = cap;
        this.ignoreFail = false;
    }

    public EnableCapHandler(String cap, boolean ignoreFail) {
        this.cap = cap;
        this.ignoreFail = ignoreFail;
    }

    @Override
    public boolean handleLS(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        if (capabilities.contains(cap)) {
            bot.sendCAP().request(cap);
        } else if (!ignoreFail) {
            throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
        } else {
            PircBotY.getLogger().log(Level.FINE, "Unsupported capability " + cap);
            return true;
        }
        PircBotY.getLogger().log(Level.FINE, "Supported capability " + cap);
        return false;
    }

    @Override
    public boolean handleACK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        return capabilities.contains(cap);
    }

    @Override
    public boolean handleNAK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        if (capabilities.contains(cap)) {
            bot.getEnabledCapabilities().remove(cap);
            if (!ignoreFail) {
                throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleUnknown(PircBotY bot, String rawLine) {
        return false;
    }

    public String getCap() {
        return cap;
    }
}

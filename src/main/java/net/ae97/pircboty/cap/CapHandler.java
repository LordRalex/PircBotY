package net.ae97.pircboty.cap;

import java.util.List;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.exception.CAPException;

public interface CapHandler {

    public boolean handleLS(PircBotY bot, List<String> capabilities) throws CAPException;

    public boolean handleACK(PircBotY bot, List<String> capabilities) throws CAPException;

    public boolean handleNAK(PircBotY bot, List<String> capabilities) throws CAPException;

    public boolean handleUnknown(PircBotY bot, String rawLine) throws CAPException;
}

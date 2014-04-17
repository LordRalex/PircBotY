package net.ae97.pircboty.cap;

import com.google.common.collect.ImmutableList;
import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.exception.CAPException;

public interface CapHandler {

    public boolean handleLS(PircBotY bot, ImmutableList<String> capabilities) throws CAPException;

    public boolean handleACK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException;

    public boolean handleNAK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException;

    public boolean handleUnknown(PircBotY bot, String rawLine) throws CAPException;
}

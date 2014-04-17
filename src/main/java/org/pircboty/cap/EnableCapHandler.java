/**
 * Copyright (C) 2010-2013
 *
 * This file is part of PircBotY.
 *
 * PircBotY is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotY is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotY. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pircboty.cap;

import com.google.common.collect.ImmutableList;
import java.util.logging.Level;
import org.pircboty.PircBotY;
import org.pircboty.exception.CAPException;

/**
 * Enables the specified capability with the server. This handler should cover
 * almost all CAP features except SASL since most only need to be requested.
 *
 * @author
 */
public class EnableCapHandler implements CapHandler {

    private final String cap;
    private final boolean ignoreFail;

    /**
     * Create EnableCapHandler not ignoring if server doesn't support the
     * requested capability
     */
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
        if (capabilities.contains(cap)) //Server supports capability, send request to use it
        {
            bot.sendCAP().request(cap);
        } else if (!ignoreFail) {
            throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
        } else {
            //Server doesn't support capability but were ignoring exceptions
            PircBotY.getLogger().log(Level.FINE, "Unsupported capability " + cap);
            return true;
        }
        PircBotY.getLogger().log(Level.FINE, "Supported capability " + cap);
        //Not finished yet
        return false;
    }

    @Override
    public boolean handleACK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        //Finished if the server is acknowledging the capability
        return capabilities.contains(cap);
    }

    @Override
    public boolean handleNAK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException {
        if (capabilities.contains(cap)) {
            //Make sure the bot didn't register this capability
            bot.getEnabledCapabilities().remove(cap);
            if (!ignoreFail) {
                throw new CAPException(CAPException.Reason.UnsupportedCapability, cap);
            } else //Nothing more to do
            {
                return true;
            }
        }
        //Not applicable to us
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

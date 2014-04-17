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
import javax.net.ssl.SSLSocketFactory;
import org.pircboty.PircBotY;
import org.pircboty.exception.CAPException;

/**
 * CAP STARTTLS support <b>*MUST BE LAST CAP HANDLER*</b>. Due to how STARTTLS
 * works and how PircBotY is designed this must be the last CAP handler,
 * otherwise you will receive an "SSL peer shutdown incorrectly" exception
 *
 * @author
 */
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
        //Not finished, still need to wait for 670 line
        return false;
    }

    @Override
    public boolean handleUnknown(PircBotY bot, String rawLine) {
        //Finished if we have successfully upgraded the socket
        return rawLine.contains(" 670 ");
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }
}

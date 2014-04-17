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
import org.pircboty.PircBotY;
import org.pircboty.exception.CAPException;

/**
 * Generic CAP handler. Relevant handle methods are called when a CAP line is
 * received. Connecting is not considered finished until {@link #isDone() }
 * returns true
 *
 * @author
 */
public interface CapHandler {

    public boolean handleLS(PircBotY bot, ImmutableList<String> capabilities) throws CAPException;

    public boolean handleACK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException;

    public boolean handleNAK(PircBotY bot, ImmutableList<String> capabilities) throws CAPException;

    public boolean handleUnknown(PircBotY bot, String rawLine) throws CAPException;
}

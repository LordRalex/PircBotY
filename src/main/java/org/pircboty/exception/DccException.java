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
package org.pircboty.exception;

import org.apache.commons.lang3.Validate;
import org.pircboty.User;

/**
 * A general exception for DCC errors
 *
 * @author
 */
public class DccException extends RuntimeException {

    private static final long serialVersionUID = 60382L;
    private final Reason ourReason;
    private final User user;

    public DccException(Reason reason, User user, String detail, Throwable cause) {
        super(generateMessage(reason, user, detail), cause);
        Validate.notNull(reason, "Reason cannot be null");
        Validate.notNull(user, "User cannot be null");
        this.ourReason = reason;
        this.user = user;
    }

    public DccException(Reason reason, User user, String detail) {
        this(reason, user, detail, null);
    }

    protected static String generateMessage(Reason reason, User user, String detail) {
        return reason + " from user " + user.getNick() + ": " + detail;
    }

    public Reason getOurReason() {
        return ourReason;
    }

    public User getUser() {
        return user;
    }

    public static enum Reason {

        UnknownFileTransferResume,
        ChatNotConnected,
        ChatCancelled,
        ChatTimeout,
        FileTransferCancelled,
        FileTransferTimeout,
        FileTransferResumeTimeout,
        FileTransferResumeCancelled,
        DccPortsInUse
    }
}

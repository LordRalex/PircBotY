/*
 * Copyright (C) 2014 Lord_Ralex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ae97.pokebot.permissions;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;
import net.ae97.pircboty.generics.GenericUserEvent;

/**
 *
 * @author Lord_Ralex
 */
public class PermissionEvent extends Event implements GenericUserEvent {

    private final User user;
    private final boolean isForced;

    public PermissionEvent(PircBotY bot, User user) {
        this(bot, user, false);
    }

    public PermissionEvent(PircBotY bot, User user, boolean forced) {
        super(bot);
        this.user = user;
        isForced = forced;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void respond(String response) {
        throw new UnsupportedOperationException("PermissionEvents are not respondable");
    }

    public boolean isForced() {
        return isForced;
    }

}

/*
 * Copyright (C) 2016 Joshua
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
package net.ae97.pircboty.api.events;

import net.ae97.pircboty.PircBotY;
import net.ae97.pircboty.User;
import net.ae97.pircboty.api.Event;

/**
 *
 * @author Joshua
 */
public class UserAuthEvent extends Event {
    
    private final User user;
    private final String old;

    public UserAuthEvent(PircBotY bot, User user, String old) {
        super(bot);
        this.user = user;
        this.old = old;
    }

    @Override
    public void respond(String response) {
        throw new UnsupportedOperationException("Attempting to respond to an user authenticated event");
    }
    
    public String getOldAccount() {
        return old;
    }
    
    public String getNew() {
        return user.getLogin();
    }
    
    public User getUser() {
        return user;
    }

}

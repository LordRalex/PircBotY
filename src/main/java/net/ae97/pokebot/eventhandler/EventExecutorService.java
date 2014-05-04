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
package net.ae97.pokebot.eventhandler;

import java.lang.reflect.Method;
import net.ae97.pokebot.api.Listener;
import net.ae97.pokebot.api.Priority;

public class EventExecutorService {

    private final Method method;
    private final Listener listener;
    private final Priority priority;
    private final boolean ignoreCancelled;

    public EventExecutorService(Listener l, Method m, Priority p) {
        this(l, m, p, false);
    }

    public EventExecutorService(Listener l, Method m, Priority p, boolean ignore) {
        method = m;
        listener = l;
        priority = p;
        ignoreCancelled = ignore;
    }

    public Listener getListener() {
        return listener;
    }

    public Priority getPriority() {
        return priority;
    }

    public Method getMethod() {
        return method;
    }

    public boolean ignoreCancelled() {
        return ignoreCancelled;
    }
}

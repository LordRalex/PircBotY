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
package net.ae97.pircboty.lang;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Lord_Ralex
 */
public class ImmutableMap<A extends Object, B extends Object> extends HashMap<A, B> {

    public ImmutableMap(Map<A, B> data) {
        super(data);
    }

    @Override
    public B put(A key, B value) {
        throw new UnsupportedOperationException("Cannot edit an immutable Map");
    }

    @Override
    public B remove(Object key) {
        throw new UnsupportedOperationException("Cannot edit an immutable Map");
    }

    @Override
    public void putAll(Map<? extends A, ? extends B> m) {
        throw new UnsupportedOperationException("Cannot edit an immutable Map");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot edit an immutable Map");
    }

}

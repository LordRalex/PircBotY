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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Lord_Ralex
 */
public class ImmutableSet<O extends Object> extends HashSet<O> {

    public ImmutableSet(Collection<O> collection) {
        super(collection);
    }

    @Override
    public Iterator<O> iterator() {
        return new ImmutableIterator<>(this);
    }

    @Override
    public boolean add(O e) {
        throw new UnsupportedOperationException("Cannot edit an immutable set");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Cannot edit an immutable set");
    }

    @Override
    public boolean addAll(Collection<? extends O> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable set");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable set");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable set");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot edit an immutable set");
    }

}

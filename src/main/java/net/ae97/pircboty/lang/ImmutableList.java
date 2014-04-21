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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImmutableList<O extends Object> extends ArrayList<O> {

    public ImmutableList(List<O> copy) {
        super(copy);
    }

    @Override
    public Iterator<O> iterator() {
        return new ImmutableIterator<>(this);
    }

    @Override
    public boolean add(O e) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public boolean addAll(Collection<? extends O> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public boolean addAll(int index, Collection<? extends O> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public O set(int index, O element) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public void add(int index, O element) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public O remove(int index) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public ListIterator<O> listIterator() {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }

    @Override
    public ListIterator<O> listIterator(int index) {
        throw new UnsupportedOperationException("Cannot edit an immutable list");
    }
}

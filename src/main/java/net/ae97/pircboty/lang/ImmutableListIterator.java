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
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Lord_Ralex
 */
public class ImmutableListIterator<O extends Object> implements ListIterator<O> {

    int index = 0;
    private final List<O> list;

    public ImmutableListIterator(Collection<O> collection) {
        list = new ArrayList<>(collection);
    }

    @Override
    public O next() {
        index++;
        return list.get(index);
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public O previous() {
        return list.get(index - 1);
    }

    @Override
    public int nextIndex() {
        return index + 1;
    }

    @Override
    public int previousIndex() {
        return index - 1;
    }

    @Override
    public void set(O e) {
        throw new UnsupportedOperationException("Cannot edit an immutable ListIterator");
    }

    @Override
    public void add(O e) {
        throw new UnsupportedOperationException("Cannot edit an immutable ListIterator");
    }

    @Override
    public boolean hasNext() {
        return index + 1 < list.size();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot edit an immutable ListIterator");
    }

}

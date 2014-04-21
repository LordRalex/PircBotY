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
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import org.apache.commons.lang3.Validate;

/**
 *
 * @author Lord_Ralex
 */
public class ImmutableIterator<O extends Object> implements Iterator<O> {

    private final Stack<O> stack = new Stack<>();

    public ImmutableIterator(Collection<O> collection) {
        O[] old = (O[]) collection.toArray();
        for (int i = old.length - 1; i >= 0; i--) {
            stack.push(old[i]);
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public O next() {
        return stack.pop();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot edit an immutable iterator");
    }

    @Override
    public void forEachRemaining(Consumer<? super O> action) {
        Validate.notNull(action, "Consumer cannot be null");
        while (hasNext()) {
            action.accept(next());
        }
    }

}

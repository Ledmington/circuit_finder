/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
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
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An immutable wrapper for the {@link java.util.Set<X>} interface. This means that
 *  operations like add, addAll, clear, remove throw UnsupportedOperationException.
 *
 * @param <X>
 *     The type of element objects.
 */
public final class ImmutableSet<X> implements Set<X> {

    // Cached UnsupportedOperationException
    private static final UnsupportedOperationException uoe =
            new UnsupportedOperationException("This is an ImmutableSet.");

    private final Set<X> s;

    /**
     * Returns a new {@code SetBuilder}.
     * Sometimes the type inference system will need a hint like
     * {@code ImmutableSet.<Object>builder()}.
     *
     * @return
     *     A new SetBuilder.
     * @param <T>
     *     The type of the elements.
     */
    public static <T> SetBuilder<T> builder() {
        return new SetBuilder<T>();
    }

    /**
     * Creates a new ImmutableSet.
     */
    public ImmutableSet() {
        this.s = new HashSet<>();
    }

    /**
     * Wraps the given Set with an Immutable interface.
     *
     * @param s
     *      The Set to be wrapped and used as Immutable.
     */
    public ImmutableSet(final Set<X> s) {
        this.s = s;
    }

    @Override
    public int size() {
        return s.size();
    }

    @Override
    public boolean isEmpty() {
        return s.isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return s.contains(obj);
    }

    @Override
    public Iterator<X> iterator() {
        return s.iterator();
    }

    @Override
    public Object[] toArray() {
        return s.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return s.toArray(a);
    }

    @Override
    public boolean add(final X x) {
        throw uoe;
    }

    @Override
    public boolean remove(final Object o) {
        throw uoe;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return s.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends X> c) {
        throw uoe;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw uoe;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw uoe;
    }

    @Override
    public void clear() {
        throw uoe;
    }
}

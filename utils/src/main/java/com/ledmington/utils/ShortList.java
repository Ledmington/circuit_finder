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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * This class represents an optimized version of {@code ArrayList<Short>}.
 */
public final class ShortList implements List<Short> {

    private static final VectorSpecies<Short> species = ShortVector.SPECIES_PREFERRED;
    private static final int lanes = species.length();

    private short[] v;
    private int size;

    public ShortList(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException(
                    String.format("Invalid initial capacity: expected >=1 but was %,d", initialCapacity));
        }
        this.v = new short[initialCapacity];
        this.size = 0;
    }

    public ShortList() {
        this(16);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(final Object o) {
        if (!o.getClass().equals(Short.class)) {
            return false;
        }
        final short s = (Short) o;
        final ShortVector vs = ShortVector.broadcast(species, s);
        int i = 0;
        for (; i + (lanes - 1) < size; i += lanes) {
            final ShortVector vi = ShortVector.fromArray(species, v, i);
            if (vs.eq(vi).anyTrue()) {
                return true;
            }
        }
        for (; i < lanes; i++) {
            if (v[i] == s) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Short> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return null;
    }

    @Override
    public boolean add(final Short aShort) {
        return false;
    }

    @Override
    public boolean remove(final Object o) {
        return false;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(final Collection<? extends Short> c) {
        return false;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Short> c) {
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {}

    @Override
    public Short get(final int index) {
        return null;
    }

    @Override
    public Short set(final int index, final Short element) {
        return null;
    }

    @Override
    public void add(final int index, final Short element) {}

    @Override
    public Short remove(final int index) {
        return null;
    }

    @Override
    public int indexOf(final Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(final Object o) {
        return 0;
    }

    @Override
    public ListIterator<Short> listIterator() {
        return null;
    }

    @Override
    public ListIterator<Short> listIterator(final int index) {
        return null;
    }

    @Override
    public List<Short> subList(final int fromIndex, final int toIndex) {
        return null;
    }

    private boolean isFull() {
        return size == v.length;
    }

    private void grow(int minimumAdditionalCapacity) {
        short[] newArray;
        try {
            newArray = new short[this.v.length * 2];
        } catch (final OutOfMemoryError oome) {
            newArray = new short[this.v.length + minimumAdditionalCapacity];
        }
        System.arraycopy(this.v, 0, newArray, 0, this.v.length);
        this.v = newArray;
    }

    public void add(final short s) {
        if (isFull()) {
            grow(1);
        }
        this.v[size++] = s;
    }

    public String toString() {
        if (size == 0) {
            return "[]";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append('[').append(v[0]);
        for (int i = 1; i < size; i++) {
            sb.append(',').append(v[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}

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
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
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
        for (; i < size; i++) {
            if (v[i] == s) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Short> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Short next() {
                return v[i++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new UnsupportedOperationException();
    }

    public void add(final short s) {
        if (isFull()) {
            grow(1);
        }
        this.v[size++] = s;
    }

    @Override
    public boolean add(final Short s) {
        this.add(s.shortValue());
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final Collection<? extends Short> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Short> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short get(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short set(final int index, final Short element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int index, final Short element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Short remove(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Short> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Short> listIterator(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Short> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException();
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

    public int hashCode() {
        ShortVector s = ShortVector.broadcast(species, (short) 17);
        int i = 0;
        for (; i + (lanes - 1) < size; i++) {
            final ShortVector vi = ShortVector.fromArray(species, v, i);
            s = s.mul((short) 31).add(vi);
        }
        if (i < size) {
            final VectorMask<Short> m = species.indexInRange(i, size);
            final ShortVector vi = ShortVector.fromArray(species, v, i, m);
            s = s.mul((short) 31).add(vi, m);
        }

        return s.reduceLanes(VectorOperators.ADD);
    }

    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        final ShortList sl = (ShortList) other;
        if (this.size != sl.size) {
            return false;
        }
        int i = 0;
        for (; i + (lanes - 1) < size; i++) {
            final ShortVector vi = ShortVector.fromArray(species, this.v, i);
            final ShortVector wi = ShortVector.fromArray(species, sl.v, i);
            if (!vi.eq(wi).allTrue()) {
                return false;
            }
        }
        for (; i < size; i++) {
            if (this.v[i] != sl.v[i]) {
                return false;
            }
        }
        return true;
    }
}

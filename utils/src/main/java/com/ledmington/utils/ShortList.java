/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2024 Filippo Barbari <filippo.barbari@gmail.com>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.IntStream;

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

    public ShortList(final Collection<Short> cs) {
        this(cs.size());
        final Iterator<Short> it = cs.iterator();
        for (int i = 0; i < cs.size(); i++) {
            this.v[i] = it.next();
        }
        this.size = cs.size();
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
        Objects.requireNonNull(o);
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
        return new ArrayList<>(IntStream.range(0, size).boxed().map(i -> v[i]).toList()).iterator();
    }

    @Override
    public Object[] toArray() {
        final Short[] newArray = new Short[this.size];
        for (int i = 0; i < size; i++) {
            newArray[i] = v[i];
        }
        return newArray;
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new UnsupportedOperationException("toArray");
    }

    public void add(final short s) {
        if (isFull()) {
            grow(1);
        }
        this.v[size++] = s;
    }

    @Override
    public boolean add(final Short s) {
        Objects.requireNonNull(s);
        this.add(s.shortValue());
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        Objects.requireNonNull(o);
        if (!o.getClass().equals(Short.class)) {
            return false;
        }
        final int index = this.indexOf(o);
        if (index == -1) {
            return false;
        }
        if (index < size) {
            System.arraycopy(v, index + 1, v, index, size - index - 1);
        }
        size--;
        return true;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException("containsAll");
    }

    @Override
    public boolean addAll(final Collection<? extends Short> c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends Short> c) {
        throw new UnsupportedOperationException("addAll with index");
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
    }

    @Override
    public void clear() {
        size = 0;
    }

    private void assertValidIndex(final int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException(String.format("Invalid index %,d for list of size %,d", index, size));
        }
    }

    @Override
    public Short get(final int index) {
        assertValidIndex(index);
        return v[index];
    }

    @Override
    public Short set(final int index, final Short element) {
        assertValidIndex(index);
        final short previous = v[index];
        v[index] = element;
        return previous;
    }

    @Override
    public void add(final int index, final Short element) {
        throw new UnsupportedOperationException("add with index");
    }

    @Override
    public Short remove(final int index) {
        assertValidIndex(index);
        final short previous = v[index];
        System.arraycopy(v, index + 1, v, index, size - index);
        size--;
        return previous;
    }

    @Override
    public int indexOf(final Object o) {
        Objects.requireNonNull(o);
        if (!o.getClass().equals(Short.class)) {
            return -1;
        }
        final short s = (Short) o;
        final ShortVector vs = ShortVector.broadcast(species, s);
        int i = 0;
        for (; i + (lanes - 1) < size; i += lanes) {
            final ShortVector vi = ShortVector.fromArray(species, v, i);
            final VectorMask<Short> m = vs.eq(vi);
            if (m.anyTrue()) {
                return i + m.firstTrue();
            }
        }
        for (; i < size; i++) {
            if (v[i] == s) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException("lastIndexOf");
    }

    @Override
    public ListIterator<Short> listIterator() {
        return new ArrayList<>(IntStream.range(0, size).boxed().map(i -> v[i]).toList()).listIterator();
    }

    @Override
    public ListIterator<Short> listIterator(final int index) {
        return new ArrayList<>(IntStream.range(0, size).boxed().map(i -> v[i]).toList()).listIterator(index);
    }

    @Override
    public List<Short> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException("subList");
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
        for (; i + (lanes - 1) < size; i += lanes) {
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
        for (; i + (lanes - 1) < size; i += lanes) {
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

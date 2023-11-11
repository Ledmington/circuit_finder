/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

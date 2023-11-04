/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * A useful class to build an {@link ImmutableSet<X>}.
 *
 * @param <X>
 *     The type of element objects.
 */
public final class SetBuilder<X> {

    private final Set<X> s = new HashSet<>();
    private boolean alreadyBuilt = false;

    /**
     * Creates an empty Set.
     */
    public SetBuilder() {}

    private void assertNotBuilt() {
        if (alreadyBuilt) {
            throw new IllegalStateException("Cannot build the same Set twice.");
        }
    }

    /**
     * Adds the given element to the Set that is being built.
     *
     * @param elem
     *      The element to be added.
     * @return
     *      The reference to this SetBuilder to allow fluent code.
     */
    public SetBuilder<X> add(final X elem) {
        assertNotBuilt();
        if (s.contains(elem)) {
            throw new IllegalArgumentException(
                    String.format("Element '%s' was already present in the Set.", elem.toString()));
        }
        s.add(elem);
        return this;
    }

    /**
     * Constructs the {@code ImmutableSet<X>} with the given data
     * and returns it.
     *
     * @return
     *      A new ImmutableSet with the given data.
     */
    public Set<X> build() {
        assertNotBuilt();
        alreadyBuilt = true;
        return new ImmutableSet<>(s);
    }
}

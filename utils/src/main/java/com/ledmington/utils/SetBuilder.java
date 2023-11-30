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

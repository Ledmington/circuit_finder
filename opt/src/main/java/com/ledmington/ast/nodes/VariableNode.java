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
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.ast.nodes;

import java.util.Objects;

public final class VariableNode extends Node {

    private final String name;

    /*
    Since the String is immutable, we can cache the hashCode.
     */
    private boolean isHashCodeSet = false;
    private int cachedHashCode = -1;

    public VariableNode(final String name) {
        this.name = Objects.requireNonNull(name);
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Invalid name: cannot be empty or blank");
        }
    }

    public String name() {
        return name;
    }

    public int size() {
        return 1;
    }

    public int compareTo(final Node other) {
        if (other instanceof ZeroNode || other instanceof OneNode) {
            return 1;
        }
        if (other instanceof VariableNode v) {
            return this.name.compareTo(v.name);
        }
        return -1;
    }

    public String toString() {
        return name;
    }

    public int hashCode() {
        if (isHashCodeSet) {
            return cachedHashCode;
        }
        cachedHashCode = name.hashCode();
        isHashCodeSet = true;
        return cachedHashCode;
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
        return this.name.equals(((VariableNode) other).name);
    }
}

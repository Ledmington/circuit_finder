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
package com.ledmington.ast.nodes;

import java.util.Map;
import java.util.Objects;

public final class VariableNode extends Node {

    private final String n;

    /*
    Since the String is immutable, we can cache the hashCode.
     */
    private boolean isHashCodeSet = false;
    private int cachedHashCode = -1;

    public VariableNode(final String name) {
        this.n = Objects.requireNonNull(name);
        if (name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Invalid name: cannot be empty or blank");
        }
    }

    public String name() {
        return n;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean evaluate(final Map<String, Boolean> values) {
        Objects.requireNonNull(values);
        if (!values.containsKey(this.n)) {
            throw new IllegalArgumentException(String.format("Cannot evaluate without value of variable '%s'", this.n));
        }
        return values.get(this.n);
    }

    @Override
    public int compareTo(final Node other) {
        if (other instanceof ZeroNode || other instanceof OneNode) {
            return 1;
        }
        if (other instanceof VariableNode v) {
            return this.n.compareTo(v.n);
        }
        return -1;
    }

    @Override
    public String toString() {
        return n;
    }

    @Override
    public int hashCode() {
        if (isHashCodeSet) {
            return cachedHashCode;
        }
        cachedHashCode = n.hashCode();
        isHashCodeSet = true;
        return cachedHashCode;
    }

    @Override
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
        return this.n.equals(((VariableNode) other).n);
    }
}

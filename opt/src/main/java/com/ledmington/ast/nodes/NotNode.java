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

import java.util.Map;
import java.util.Objects;

public final class NotNode extends Node {

    private final Node inner;

    /*
    Since all Nodes are immutable, we can cache the size, the hashCode and the String representation.
     */
    private boolean isSizeSet = false;
    private int cachedSize = -1;
    private boolean isHashCodeSet = false;
    private int cachedHashCode = -1;
    private boolean isStringSet = false;
    private String cachedString = null;

    public NotNode(final Node inner) {
        this.inner = Objects.requireNonNull(inner);
    }

    public Node inner() {
        return inner;
    }

    public int size() {
        if (isSizeSet) {
            return cachedSize;
        }
        cachedSize = 1 + inner.size();
        isSizeSet = true;
        return cachedSize;
    }

    public boolean evaluate(final Map<String, Boolean> values) {
        Objects.requireNonNull(values);
        return !inner.evaluate(values);
    }

    public int compareTo(final Node other) {
        if (other instanceof ZeroNode || other instanceof OneNode || other instanceof VariableNode) {
            return 1;
        }
        if (other instanceof MultiNode) {
            return -1;
        }
        return inner.compareTo(((NotNode) other).inner);
    }

    public String toString() {
        if (isStringSet) {
            return cachedString;
        }
        if (inner instanceof AndNode || inner instanceof OrNode) {
            cachedString = "~(" + inner.toString() + ")";
        } else {
            cachedString = "~" + inner.toString();
        }
        isStringSet = true;
        return cachedString;
    }

    public int hashCode() {
        if (isHashCodeSet) {
            return cachedHashCode;
        }
        cachedHashCode = inner.hashCode();
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
        return this.inner.equals(((NotNode) other).inner);
    }
}

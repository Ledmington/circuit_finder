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

public final class NotNode extends Node {
    private final Node inner;

    public NotNode(final Node inner) {
        this.inner = Objects.requireNonNull(inner);
    }

    public Node inner() {
        return inner;
    }

    public int size() {
        return 1 + inner.size();
    }

    public String toString() {
        if (inner instanceof AndNode || inner instanceof OrNode) {
            return "~(" + inner.toString() + ")";
        }
        return "~" + inner.toString();
    }

    public int hashCode() {
        return inner.hashCode();
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

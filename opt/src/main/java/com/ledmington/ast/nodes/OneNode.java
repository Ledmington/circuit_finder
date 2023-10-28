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

public final class OneNode extends Node {

    public int size() {
        return 1;
    }

    public boolean evaluate(final Map<String, Boolean> values) {
        Objects.requireNonNull(values);
        return true;
    }

    public int compareTo(final Node other) {
        if (other instanceof ZeroNode) {
            return 1;
        }
        if (other instanceof OneNode) {
            return 0;
        }
        return -1;
    }

    public String toString() {
        return "1";
    }

    public int hashCode() {
        return 1;
    }

    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        return this.getClass().equals(other.getClass());
    }
}

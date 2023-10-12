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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class AndNode extends Node {

    private final List<Node> nodes;

    public AndNode(final List<Node> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
        if (nodes.size() < 2) {
            throw new IllegalArgumentException(
                    String.format("Invalid list of nodes: should have had >=2 elements but had %,d", nodes.size()));
        }
    }

    public List<Node> nodes() {
        return nodes;
    }

    public int size() {
        int s = 1;
        for (final Node n : nodes) {
            s += n.size();
        }
        return s;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Consumer<Node> c = n -> {
            if (n instanceof OrNode) {
                sb.append("(").append(n).append(")");
            } else {
                sb.append(n.toString());
            }
        };
        c.accept(nodes.get(0));
        for (int i = 1; i < nodes.size(); i++) {
            sb.append('&');
            c.accept(nodes.get(i));
        }
        return sb.toString();
    }

    public int hashCode() {
        return nodes.hashCode();
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

        return this.nodes.equals(((AndNode) other).nodes);
    }
}

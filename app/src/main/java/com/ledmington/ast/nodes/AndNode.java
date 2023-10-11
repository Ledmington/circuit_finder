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
import java.util.stream.Collectors;

public final class AndNode extends Node {

    private final List<Node> nodes;

    public AndNode(final List<Node> nodes) {
        this.nodes = Objects.requireNonNull(nodes);
        if (nodes.size() < 2) {
            throw new IllegalArgumentException(
                    String.format("Invalid list of nodes: should have had >=2 elements but had %,d", nodes.size()));
        }
    }

    public String toString() {
        return nodes.stream().map(Object::toString).collect(Collectors.joining("&"));
    }

    public int hashCode() {
        return nodes.hashCode();
    }
}

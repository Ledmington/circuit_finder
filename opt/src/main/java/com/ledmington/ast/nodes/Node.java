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

import java.util.List;
import java.util.Map;

public abstract sealed class Node implements Comparable<Node>
        permits MultiNode, NotNode, OneNode, VariableNode, ZeroNode {

    /**
     * Computes the size of the AST this Node is root of in terms of number of nodes.
     */
    public abstract int size();

    public abstract boolean evaluate(final Map<String, Boolean> values);

    /**
     * Utility method to create an AndNode only when there are >= 2 nodes.
     * If an empty List is passed, a OneNode is returned.
     * If a List with one element is passed, the single element is returned.
     */
    public static Node and(final List<Node> nodes) {
        return switch (nodes.size()) {
            case 0 -> new OneNode();
            case 1 -> nodes.get(0);
            default -> new AndNode(nodes);
        };
    }

    /**
     * Utility method to create an OrNode only when there are >= 2 nodes.
     * If an empty List is passed, a ZeroNode is returned.
     * If a List with one element is passed, the single element is returned.
     */
    public static Node or(final List<Node> nodes) {
        return switch (nodes.size()) {
            case 0 -> new ZeroNode();
            case 1 -> nodes.get(0);
            default -> new OrNode(nodes);
        };
    }
}

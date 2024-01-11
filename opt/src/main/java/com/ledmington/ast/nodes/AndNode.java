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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class AndNode extends MultiNode {

    private static final int minChildNodes = 2;

    private final List<Node> children;

    /*
    Since all Nodes are immutable, we can cache the size, the hashCode and the String representation.
     */
    private boolean isSizeSet = false;
    private int cachedSize = -1;
    private boolean isHashCodeSet = false;
    private int cachedHashCode = -1;
    private boolean isStringSet = false;
    private String cachedString = null;

    public AndNode(final List<Node> children) {
        this.children = new ArrayList<>(Objects.requireNonNull(children));
        Collections.sort(this.children);
        if (children.size() < minChildNodes) {
            throw new IllegalArgumentException(
                    String.format("Invalid list of nodes: should have had >=2 elements but had %,d", children.size()));
        }
    }

    @Override
    public List<Node> nodes() {
        return children;
    }

    public boolean contains(final Node n) {
        final int idx = Collections.binarySearch(this.children, n);
        return idx >= 0 && idx < this.children.size() && n.equals(this.children.get(idx));
    }

    @Override
    public int size() {
        if (isSizeSet) {
            return cachedSize;
        }
        int s = 1;
        for (final Node n : children) {
            s += n.size();
        }
        cachedSize = s;
        isSizeSet = true;
        return s;
    }

    @Override
    public boolean evaluate(final Map<String, Boolean> values) {
        Objects.requireNonNull(values);
        for (final Node n : children) {
            if (!n.evaluate(values)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(final Node other) {
        if (other instanceof ZeroNode
                || other instanceof OneNode
                || other instanceof VariableNode
                || other instanceof NotNode) {
            return 1;
        }
        if (other instanceof OrNode) {
            return -1;
        }
        final AndNode and = (AndNode) other;
        int i = 0;
        for (; i < this.children.size() && i < and.nodes().size(); i++) {
            final int r = this.children.get(i).compareTo(and.nodes().get(i));
            if (r != 0) {
                return r;
            }
        }
        if (i < this.children.size()) {
            return 1;
        }
        if (i < and.children.size()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        if (isStringSet) {
            return cachedString;
        }
        final StringBuilder sb = new StringBuilder();
        final Consumer<Node> c = n -> {
            if (n instanceof OrNode) {
                sb.append("(").append(n).append(")");
            } else {
                sb.append(n.toString());
            }
        };
        c.accept(children.get(0));
        for (int i = 1; i < children.size(); i++) {
            sb.append('&');
            c.accept(children.get(i));
        }
        cachedString = sb.toString();
        isStringSet = true;
        return cachedString;
    }

    @Override
    public int hashCode() {
        if (isHashCodeSet) {
            return cachedHashCode;
        }
        cachedHashCode = children.hashCode();
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

        // Checking equals ignoring order of elements
        final List<Node> mynodes = this.children;
        final List<Node> othernodes = ((AndNode) other).children;
        if (mynodes.size() != othernodes.size()) {
            return false;
        }

        final boolean[] visited = new boolean[mynodes.size()];
        Arrays.fill(visited, false);
        for (final Node mynode : mynodes) {
            for (int j = 0; j < othernodes.size(); j++) {
                if (visited[j]) {
                    continue;
                }
                if (mynode.equals(othernodes.get(j))) {
                    visited[j] = true;
                    break;
                }
            }
        }

        for (final boolean b : visited) {
            if (!b) {
                return false;
            }
        }
        return true;
    }
}

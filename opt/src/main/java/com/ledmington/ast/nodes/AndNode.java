/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

    private final List<Node> nodes;

    /*
    Since all Nodes are immutable, we can cache the size, the hashCode and the String representation.
     */
    private boolean isSizeSet = false;
    private int cachedSize = -1;
    private boolean isHashCodeSet = false;
    private int cachedHashCode = -1;
    private boolean isStringSet = false;
    private String cachedString = null;

    public AndNode(final List<Node> nodes) {
        this.nodes = new ArrayList<>(Objects.requireNonNull(nodes));
        Collections.sort(this.nodes);
        if (nodes.size() < 2) {
            throw new IllegalArgumentException(
                    String.format("Invalid list of nodes: should have had >=2 elements but had %,d", nodes.size()));
        }
    }

    public List<Node> nodes() {
        return nodes;
    }

    public boolean contains(final Node n) {
        final int idx = Collections.binarySearch(this.nodes, n);
        return idx >= 0 && idx < this.nodes.size() && n.equals(this.nodes.get(idx));
    }

    public int size() {
        if (isSizeSet) {
            return cachedSize;
        }
        int s = 1;
        for (final Node n : nodes) {
            s += n.size();
        }
        cachedSize = s;
        isSizeSet = true;
        return s;
    }

    public boolean evaluate(final Map<String, Boolean> values) {
        Objects.requireNonNull(values);
        for (final Node n : nodes) {
            if (!n.evaluate(values)) {
                return false;
            }
        }
        return true;
    }

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
        for (; i < this.nodes.size() && i < and.nodes().size(); i++) {
            final int r = this.nodes.get(i).compareTo(and.nodes().get(i));
            if (r != 0) {
                return r;
            }
        }
        if (i < this.nodes.size()) {
            return 1;
        }
        if (i < and.nodes.size()) {
            return -1;
        }
        return 0;
    }

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
        c.accept(nodes.get(0));
        for (int i = 1; i < nodes.size(); i++) {
            sb.append('&');
            c.accept(nodes.get(i));
        }
        cachedString = sb.toString();
        isStringSet = true;
        return cachedString;
    }

    public int hashCode() {
        if (isHashCodeSet) {
            return cachedHashCode;
        }
        cachedHashCode = nodes.hashCode();
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

        // Checking equals ignoring order of elements
        final List<Node> mynodes = this.nodes;
        final List<Node> othernodes = ((AndNode) other).nodes;
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

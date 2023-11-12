/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.nodes;

import java.util.Map;
import java.util.Objects;

public final class NotNode extends Node {

    private final Node n;

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
        this.n = Objects.requireNonNull(inner);
    }

    public Node inner() {
        return n;
    }

    @Override
    public int size() {
        if (isSizeSet) {
            return cachedSize;
        }
        cachedSize = 1 + n.size();
        isSizeSet = true;
        return cachedSize;
    }

    @Override
    public boolean evaluate(final Map<String, Boolean> values) {
        Objects.requireNonNull(values);
        return !n.evaluate(values);
    }

    @Override
    public int compareTo(final Node other) {
        if (other instanceof ZeroNode || other instanceof OneNode || other instanceof VariableNode) {
            return 1;
        }
        if (other instanceof MultiNode) {
            return -1;
        }
        return n.compareTo(((NotNode) other).n);
    }

    @Override
    public String toString() {
        if (isStringSet) {
            return cachedString;
        }
        if (n instanceof AndNode || n instanceof OrNode) {
            cachedString = "~(" + n.toString() + ")";
        } else {
            cachedString = "~" + n.toString();
        }
        isStringSet = true;
        return cachedString;
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
        return this.n.equals(((NotNode) other).n);
    }
}

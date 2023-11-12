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

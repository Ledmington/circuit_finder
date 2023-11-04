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

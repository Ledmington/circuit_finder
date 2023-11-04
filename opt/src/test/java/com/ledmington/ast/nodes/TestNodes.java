/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestNodes {

    @Test
    public void invalidNot() {
        assertThrows(NullPointerException.class, () -> new NotNode(null));
    }

    @Test
    public void invalidAnd() {
        assertThrows(NullPointerException.class, () -> new AndNode(null));
        assertThrows(IllegalArgumentException.class, () -> new AndNode(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new AndNode(List.of(new ZeroNode())));
    }

    @Test
    public void invalidOr() {
        assertThrows(NullPointerException.class, () -> new OrNode(null));
        assertThrows(IllegalArgumentException.class, () -> new OrNode(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new OrNode(List.of(new ZeroNode())));
    }

    @Test
    public void invalidVariable() {
        assertThrows(NullPointerException.class, () -> new VariableNode(null));
        assertThrows(IllegalArgumentException.class, () -> new VariableNode(""));
        assertThrows(IllegalArgumentException.class, () -> new VariableNode(" "));
    }

    @Test
    public void andEqualsIgnoreOrder() {
        assertEquals(
                new AndNode(List.of(new ZeroNode(), new OneNode())),
                new AndNode(List.of(new OneNode(), new ZeroNode())));
    }

    @Test
    public void orEqualsIgnoreOrder() {
        assertEquals(
                new OrNode(List.of(new ZeroNode(), new OneNode())), new OrNode(List.of(new OneNode(), new ZeroNode())));
    }
}

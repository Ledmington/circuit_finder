/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast;

import java.util.Arrays;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.VariableNode;
import com.ledmington.ast.nodes.ZeroNode;

/**
 * This class exists only to provide common utility methods for testing.
 */
public abstract class TestOptimizer {

    protected static final Optimizer opt = new Optimizer(1);

    protected static Node zero() {
        return new ZeroNode();
    }

    protected static Node one() {
        return new OneNode();
    }

    protected static Node not(final Node n) {
        return new NotNode(n);
    }

    protected static Node A() {
        return new VariableNode("A");
    }

    protected static Node B() {
        return new VariableNode("B");
    }

    protected static Node C() {
        return new VariableNode("C");
    }

    protected static Node D() {
        return new VariableNode("D");
    }

    protected static Node E() {
        return new VariableNode("E");
    }

    protected static Node or(final Node... nodes) {
        return new OrNode(Arrays.asList(nodes));
    }

    protected static Node and(final Node... nodes) {
        return new AndNode(Arrays.asList(nodes));
    }
}

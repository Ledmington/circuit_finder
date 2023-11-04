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

public abstract sealed class Node implements Comparable<Node>
        permits MultiNode, NotNode, OneNode, VariableNode, ZeroNode {

    /**
     * Computes the size of the AST this Node is root of in terms of number of nodes.
     */
    public abstract int size();

    public abstract boolean evaluate(final Map<String, Boolean> values);
}

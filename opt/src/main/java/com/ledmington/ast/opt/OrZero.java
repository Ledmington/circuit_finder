/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.opt;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.ZeroNode;

/**
 * This class covers the following cases:
 *  1 + 0 = 1
 *  A + 0 = A
 */
public final class OrZero implements Optimization {
    @Override
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or && or.contains(new ZeroNode())) {
            final List<Node> tmp =
                    or.nodes().stream().filter(n -> !(n instanceof ZeroNode)).toList();
            final Node result = Node.or(tmp);
            return Optional.of(new OptimizationResult(-root.size() + result.size(), result));
        }

        return Optional.empty();
    }
}

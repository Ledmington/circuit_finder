/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.opt;

import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;

/**
 * This class covers the following cases:
 *  1 + 1 = 1
 *  A + 1 = 1
 */
public final class OrOne implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or && or.contains(new OneNode())) {
            return Optional.of(new OptimizationResult(-root.size() + 1, new OneNode()));
        }

        return Optional.empty();
    }
}

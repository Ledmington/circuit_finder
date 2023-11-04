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

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.ZeroNode;

/**
 * This class covers the following cases:
 *  1 & 0 = 0
 *  A & 0 = 0
 */
public final class AndZero implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof AndNode and && and.contains(new ZeroNode())) {
            return Optional.of(new OptimizationResult(-root.size() + 1, new ZeroNode()));
        }

        return Optional.empty();
    }
}

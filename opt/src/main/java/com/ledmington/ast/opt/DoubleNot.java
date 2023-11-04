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
import com.ledmington.ast.nodes.NotNode;

/**
 * This class covers the following cases:
 *  ~~A = A
 */
public final class DoubleNot implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof NotNode not && not.inner() instanceof NotNode notnot) {
            return Optional.of(new OptimizationResult(-2, notnot.inner()));
        }

        return Optional.empty();
    }
}

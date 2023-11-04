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
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.ZeroNode;

/**
 * This class represents the following cases:
 *  ~0 = 1
 *  ~1 = 0
 */
public final class NotConstant implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof NotNode not) {
            if (not.inner() instanceof ZeroNode) {
                return Optional.of(new OptimizationResult(-1, new OneNode()));
            }
            if (not.inner() instanceof OneNode) {
                return Optional.of(new OptimizationResult(-1, new ZeroNode()));
            }
        }

        return Optional.empty();
    }
}

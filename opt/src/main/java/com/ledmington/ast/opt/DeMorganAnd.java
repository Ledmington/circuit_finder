/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OrNode;

/**
 * This class covers the following cases:
 * ~(~A & ~B) = A + B
 * ~(A & ~B) = ~A + B
 * ~(~A & B) = A + ~B
 * ~A & ~B = ~(A + B)
 */
public final class DeMorganAnd implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof NotNode not && not.inner() instanceof AndNode and) {
            // we are in the case ~(... & ...)
            int score = -1; // we start from -1 because we know in advance that we will at least remove the root NotNode
            final List<Node> tmp = new ArrayList<>();
            for (final Node n : and.nodes()) {
                if (n instanceof NotNode notnot) {
                    tmp.add(notnot.inner());
                    score--;
                } else {
                    tmp.add(new NotNode(n));
                    score++;
                }
            }

            if (score >= 0) {
                return Optional.empty();
            }

            return Optional.of(new OptimizationResult(score, new OrNode(tmp)));
        } else if (root instanceof AndNode and) {
            // we are in the case ~... & ~...
            int score = 1; // we start from 1 because we know in advance that we will at least add the root NotNode
            final List<Node> tmp = new ArrayList<>();
            for (final Node n : and.nodes()) {
                if (n instanceof NotNode notnot) {
                    tmp.add(notnot.inner());
                    score--;
                } else {
                    tmp.add(new NotNode(n));
                    score++;
                }
            }

            if (score >= 0) {
                return Optional.empty();
            }

            return Optional.of(new OptimizationResult(score, new NotNode(new OrNode(tmp))));
        }

        return Optional.empty();
    }
}

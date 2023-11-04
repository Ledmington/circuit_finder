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

import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OrNode;

/**
 * This classs covers the following cases:
 * A + (B + C) = A + B + C
 * (A + B) + (C + D) = A + B + C + D
 */
public final class MergeOr implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or) {
            final List<Node> tmp = new ArrayList<>();
            int removed = 0;
            for (final Node n : or.nodes()) {
                if (n instanceof OrNode inneror) {
                    tmp.addAll(inneror.nodes());
                    removed++;
                } else {
                    tmp.add(n);
                }
            }

            if (tmp.size() > or.nodes().size()) {
                return Optional.of(new OptimizationResult(-removed, new OrNode(tmp)));
            }
        }

        return Optional.empty();
    }
}

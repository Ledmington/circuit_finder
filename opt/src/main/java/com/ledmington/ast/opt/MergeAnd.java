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

/**
 * This classs covers the following cases:
 * A & (B & C) = A & B & C
 * (A & B) & (C & D) = A & B & C & D
 */
public final class MergeAnd implements Optimization {
    @Override
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof AndNode and) {
            final List<Node> tmp = new ArrayList<>();
            int removed = 0;
            for (final Node n : and.nodes()) {
                if (n instanceof AndNode innerand) {
                    tmp.addAll(innerand.nodes());
                    removed++;
                } else {
                    tmp.add(n);
                }
            }

            if (tmp.size() > and.nodes().size()) {
                return Optional.of(new OptimizationResult(-removed, new AndNode(tmp)));
            }
        }

        return Optional.empty();
    }
}

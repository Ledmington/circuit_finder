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
import com.ledmington.ast.nodes.OrNode;

/**
 * This class covers the following cases:
 * A & (A + B) = A
 */
public final class AndAbsorption implements Optimization {
    @Override
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof AndNode and) {
            final List<Node> normals = new ArrayList<>();
            final List<OrNode> ors = new ArrayList<>();
            for (final Node n : and.nodes()) {
                if (n instanceof OrNode or) {
                    ors.add(or);
                } else {
                    normals.add(n);
                }
            }

            // if there are no OrNodes, we exit
            if (ors.isEmpty()) {
                return Optional.empty();
            }

            final List<Node> tmp = new ArrayList<>();
            int score = 0;
            boolean atLeastOne = false;
            for (final OrNode or : ors) {
                // in each iteration we check if the selected OrNode must be deleted or not
                boolean valid = true;
                for (final Node n : and.nodes()) {
                    if (n == or) {
                        continue;
                    }
                    if (or.contains(n)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    tmp.add(or);
                } else {
                    atLeastOne = true;
                    score -= or.size();
                }
            }

            if (!atLeastOne) {
                return Optional.empty();
            }

            tmp.addAll(normals);

            if (tmp.size() == 1) {
                // the score returned counts also the removal of the parent AndNode
                return Optional.of(new OptimizationResult(score - 1, tmp.get(0)));
            }

            return Optional.of(new OptimizationResult(score, new AndNode(tmp)));
        }

        return Optional.empty();
    }
}

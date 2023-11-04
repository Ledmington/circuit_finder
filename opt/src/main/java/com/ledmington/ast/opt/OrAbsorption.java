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
 * A + (A & B) = A
 */
public final class OrAbsorption implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or) {
            final List<Node> normals = new ArrayList<>();
            final List<AndNode> ands = new ArrayList<>();
            for (final Node n : or.nodes()) {
                if (n instanceof AndNode and) {
                    ands.add(and);
                } else {
                    normals.add(n);
                }
            }

            // if there are no AndNodes, we exit
            if (ands.isEmpty()) {
                return Optional.empty();
            }

            final List<Node> tmp = new ArrayList<>();
            int score = 0;
            boolean atLeastOne = false;
            for (final AndNode and : ands) {
                // in each iteration we check if the selected AndNode must be deleted or not
                boolean valid = true;
                for (final Node n : or.nodes()) {
                    if (n == and) {
                        continue;
                    }
                    if (and.contains(n)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    tmp.add(and);
                } else {
                    atLeastOne = true;
                    score -= and.size();
                }
            }

            if (!atLeastOne) {
                return Optional.empty();
            }

            tmp.addAll(normals);

            if (tmp.size() == 1) {
                // the score returned counts also the removal of the parent OrNode
                return Optional.of(new OptimizationResult(score - 1, tmp.get(0)));
            }

            return Optional.of(new OptimizationResult(score, new OrNode(tmp)));
        }

        return Optional.empty();
    }
}

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

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OneNode;

/**
 * This class covers the following cases:
 *  1 & 1 = 1
 *  A & 1 = A
 */
public final class AndOne implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof AndNode and && and.contains(new OneNode())) {
            final List<Node> tmp =
                    and.nodes().stream().filter(n -> !(n instanceof OneNode)).toList();
            if (tmp.isEmpty()) {
                return Optional.of(new OptimizationResult(-root.size() + 1, new OneNode()));
            }
            if (tmp.size() == 1) {
                return Optional.of(new OptimizationResult(-root.size() + 1, tmp.get(0)));
            }
            return Optional.of(new OptimizationResult(-root.size() + (tmp.size() + 1), new AndNode(tmp)));
        }

        return Optional.empty();
    }
}

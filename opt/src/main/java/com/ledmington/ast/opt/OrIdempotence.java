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
import java.util.TreeSet;

import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OrNode;

/**
 * This class covers the following cases:
 * A + A = A
 */
public final class OrIdempotence implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or) {
            final List<Node> tmp = new ArrayList<>(new TreeSet<>(or.nodes()));

            if (tmp.size() < or.nodes().size()) {
                if (tmp.size() == 1) {
                    return Optional.of(new OptimizationResult(-or.size() + 1, tmp.get(0)));
                }
                return Optional.of(new OptimizationResult(-or.size() + (tmp.size() + 1), new OrNode(tmp)));
            }
        }

        return Optional.empty();
    }
}

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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;

/**
 * This class covers the following cases:
 *  A + ~A = 1
 */
public final class OrComplementation implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or) {
            final List<Node> normals = new ArrayList<>(
                    or.nodes().stream().filter(n -> !(n instanceof NotNode)).toList());
            final List<Node> nots = new ArrayList<>(
                    or.nodes().stream().filter(n -> n instanceof NotNode).toList());
            final List<Node> tmp = new ArrayList<>();
            boolean found = false;
            int score = 0;
            final Iterator<Node> it = normals.iterator();
            while (it.hasNext()) {
                final Node n = it.next();
                if (nots.contains(new NotNode(n))) {
                    it.remove();
                    nots.remove(new NotNode(n));
                    found = true;
                    score -= (2 * n.size() + 1);
                } else {
                    tmp.add(n);
                }
            }

            if (found) {
                tmp.addAll(nots);
                tmp.add(new OneNode());

                if (tmp.size() == 1) {
                    return Optional.of(new OptimizationResult(score, new OneNode()));
                }
                return Optional.of(new OptimizationResult(score + 1, new OrNode(tmp)));
            }
        }

        return Optional.empty();
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OrNode;

/**
 * This class covers the following cases:
 * (A + B) & (A + C) = A + (B & C)
 * (A + B) & (A + C) & D = (A + (B & C)) & D
 */
public final class ReverseOrDistributivity implements Optimization {
    @Override
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        final int minAndChildNodes = 2;

        if (root instanceof AndNode and) {
            final List<OrNode> ors = new ArrayList<>();
            for (final Node n : and.nodes()) {
                if (n instanceof OrNode or) {
                    ors.add(or);
                }
            }

            // this optimization needs at least two OrsNodes to group a common part
            if (ors.size() < minAndChildNodes) {
                return Optional.empty();
            }

            // we need to find a single Node that is common to most of the inner nodes
            final Map<Node, Integer> counts = new HashMap<>();
            for (final OrNode or : ors) {
                for (final Node n : or.nodes()) {
                    if (counts.containsKey(n)) {
                        counts.put(n, counts.get(n) + 1);
                    } else {
                        counts.put(n, 1);
                    }
                }
            }

            final Node maxCommonNode = counts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElseThrow();
            if (counts.get(maxCommonNode) == 1) {
                return Optional.empty();
            }

            final List<OrNode> nodesWithCommon = new ArrayList<>();
            final List<Node> nodesWithoutCommon = new ArrayList<>();
            for (final Node n : and.nodes()) {
                if (n instanceof OrNode or && or.contains(maxCommonNode)) {
                    nodesWithCommon.add(or);
                } else {
                    nodesWithoutCommon.add(n);
                }
            }

            // we need to re-add one CommonNode because we are grouping it outside
            // we need to also add a new OrNode outside
            int score = 2;

            final List<Node> tmp = new ArrayList<>();
            for (final OrNode or : nodesWithCommon) {
                // from each of these nodes we remove the common node
                score--;
                if (or.nodes().size() == minAndChildNodes) {
                    // from AndNodes with two inners we also remove the parent AndNode
                    score--;
                    if (or.nodes().get(0).equals(maxCommonNode)) {
                        tmp.add(or.nodes().get(1));
                    } else {
                        tmp.add(or.nodes().get(0));
                    }
                } else {
                    tmp.add(new OrNode(or.nodes().stream()
                            .filter(n -> !n.equals(maxCommonNode))
                            .toList()));
                }
            }

            if (nodesWithoutCommon.isEmpty()) {
                // if no other Node is left, we can also remove the parent AndNode
                return Optional.of(new OptimizationResult(score, new OrNode(List.of(maxCommonNode, new AndNode(tmp)))));
            } else {
                nodesWithoutCommon.add(new OrNode(List.of(maxCommonNode, new AndNode(tmp))));
                return Optional.of(new OptimizationResult(score + 1, new AndNode(nodesWithoutCommon)));
            }
        }

        return Optional.empty();
    }
}

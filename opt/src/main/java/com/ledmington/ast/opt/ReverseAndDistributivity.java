/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
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
 * (A & B) + (A & C) = A & (B + C)
 * (A & B) + (A & C) + D = (A & (B + C)) + D
 */
public final class ReverseAndDistributivity implements Optimization {
    @Override
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        final int minAndChildNodes = 2;

        if (root instanceof OrNode or) {
            final List<AndNode> ands = new ArrayList<>();
            for (final Node n : or.nodes()) {
                if (n instanceof AndNode and) {
                    ands.add(and);
                }
            }

            // this optimization needs at least two AndNodes to group a common part
            if (ands.size() < minAndChildNodes) {
                return Optional.empty();
            }

            // we need to find a single Node that is common to most of the inner nodes
            final Map<Node, Integer> counts = new HashMap<>();
            for (final AndNode and : ands) {
                for (final Node n : and.nodes()) {
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

            final List<AndNode> nodesWithCommon = new ArrayList<>();
            final List<Node> nodesWithoutCommon = new ArrayList<>();
            for (final Node n : or.nodes()) {
                if (n instanceof AndNode and && and.contains(maxCommonNode)) {
                    nodesWithCommon.add(and);
                } else {
                    nodesWithoutCommon.add(n);
                }
            }

            // we need to re-add one CommonNode because we are grouping it outside
            // we need to also add a new AndNode outside
            int score = 2;

            final List<Node> tmp = new ArrayList<>();
            for (final AndNode and : nodesWithCommon) {
                // from each of these nodes we remove the common node
                score--;
                if (and.nodes().size() == minAndChildNodes) {
                    // from AndNodes with two inners we also remove the parent AndNode
                    score--;
                    if (and.nodes().get(0).equals(maxCommonNode)) {
                        tmp.add(and.nodes().get(1));
                    } else {
                        tmp.add(and.nodes().get(0));
                    }
                } else {
                    tmp.add(new AndNode(and.nodes().stream()
                            .filter(n -> !n.equals(maxCommonNode))
                            .toList()));
                }
            }

            if (nodesWithoutCommon.isEmpty()) {
                // if no other Node is left, we can also remove the parent OrNode
                return Optional.of(new OptimizationResult(score, new AndNode(List.of(maxCommonNode, new OrNode(tmp)))));
            } else {
                nodesWithoutCommon.add(new AndNode(List.of(maxCommonNode, new OrNode(tmp))));
                return Optional.of(new OptimizationResult(score + 1, new OrNode(nodesWithoutCommon)));
            }
        }

        return Optional.empty();
    }
}

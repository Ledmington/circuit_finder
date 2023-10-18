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
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.ast.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OrNode;

public final class ReverseAndDistributivity implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof OrNode or) {
            final List<AndNode> ands = new ArrayList<>();
            for (final Node n : or.nodes()) {
                if (n instanceof AndNode and) {
                    ands.add(and);
                }
            }

            // this optimization needs at least two AndNodes to group a common part
            if (ands.size() < 2) {
                return Optional.empty();
            }

            List<Node> maxCommon = List.of();
            int maxCommonSize = 0;
            AndNode first = null;
            AndNode second = null;
            for (int i = 0; i < ands.size(); i++) {
                final AndNode a = ands.get(i);
                for (int j = i + 1; j < ands.size(); j++) {
                    final AndNode b = ands.get(j);

                    // here we count how many common nodes A and B have
                    final List<Node> commonNodes = new ArrayList<>();
                    int commonNodeSize = 0;
                    for (final Node n : a.nodes()) {
                        if (b.nodes().contains(n)) {
                            commonNodes.add(n);
                            commonNodeSize += n.size();
                        }
                    }

                    if (commonNodes.size() > maxCommon.size()) {
                        maxCommon = commonNodes;
                        maxCommonSize = commonNodeSize;
                        first = a;
                        second = b;
                    }
                }
            }

            // if no common parts are found, this optimization can not be applied
            if (maxCommon.isEmpty()) {
                return Optional.empty();
            }

            final List<Node> tmp = new ArrayList<>(maxCommon);
            final List<Node> innerOr = new ArrayList<>();
            final List<Node> finalMaxCommon = maxCommon;
            int score = 0;
            if (maxCommon.size() - first.nodes().size() > 1) {
                innerOr.add(new AndNode(first.nodes().stream()
                        .filter(n -> !finalMaxCommon.contains(n))
                        .toList()));
                score -= maxCommonSize;
            } else {
                innerOr.add(first.nodes().stream()
                        .filter(n -> !finalMaxCommon.contains(n))
                        .findFirst()
                        .orElseThrow());
                // we also need to remove the parent AndNode
                score -= maxCommonSize - 1;
            }
            if (maxCommon.size() - second.nodes().size() > 1) {
                innerOr.add(new AndNode(second.nodes().stream()
                        .filter(n -> !finalMaxCommon.contains(n))
                        .toList()));
                score -= maxCommonSize;
            } else {
                innerOr.add(second.nodes().stream()
                        .filter(n -> !finalMaxCommon.contains(n))
                        .findFirst()
                        .orElseThrow());
                // we also need to remove the parent AndNode
                score -= maxCommonSize - 1;
            }
            tmp.add(new OrNode(innerOr));

            return Optional.of(new OptimizationResult(score + maxCommonSize, new AndNode(tmp)));
        }

        return Optional.empty();
    }
}

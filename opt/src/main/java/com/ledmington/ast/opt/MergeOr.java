/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2024 Filippo Barbari <filippo.barbari@gmail.com>
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
    @Override
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

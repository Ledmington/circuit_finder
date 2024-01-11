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
    @Override
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof AndNode and && and.contains(new OneNode())) {
            final List<Node> tmp =
                    and.nodes().stream().filter(n -> !(n instanceof OneNode)).toList();
            final Node result = Node.and(tmp);
            return Optional.of(new OptimizationResult(-root.size() + result.size(), result));
        }

        return Optional.empty();
    }
}

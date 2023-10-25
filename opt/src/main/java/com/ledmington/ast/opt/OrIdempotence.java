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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            final List<Node> tmp = new ArrayList<>(new HashSet<>(or.nodes()));

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
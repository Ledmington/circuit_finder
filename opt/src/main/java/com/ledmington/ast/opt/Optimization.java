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

import java.util.Optional;

import com.ledmington.ast.nodes.Node;

public interface Optimization {

    /**
     * Checks whether this optimization can be performed on the given AST.
     * If this optimization is not possible, an empty Optional is returned.
     * If this optimization is possible, an OptimizationResult containing the
     * relevant information is returned.
     *
     * @param root
     *      The root of the AST where to apply this optimization.
     * @return
     *      An empty Optional if this optimization cannot be applied.
     *      An OptimizationResult with the relevant information otherwise.
     */
    Optional<OptimizationResult> check(final Node root);
}

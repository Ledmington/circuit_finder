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

import com.ledmington.ast.nodes.Node;

/**
 * An object containing the relevant information to perform an optimization.
 *
 * @param score
 * 		The score of this optimization. The score is just the difference of
 * 		the number of nodes between the new AST and the old one.
 * @param result
 * 		The root of the new AST.
 */
public record OptimizationResult(int score, Node result) {}

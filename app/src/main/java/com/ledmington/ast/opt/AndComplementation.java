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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.ZeroNode;

/**
 * This class covers the following cases:
 *  A & ~A = 0
 */
public final class AndComplementation implements Optimization {
    public Optional<OptimizationResult> check(final Node root) {
        Objects.requireNonNull(root);

        if (root instanceof AndNode and) {
            final List<Node> normals = new ArrayList<>(
                    and.nodes().stream().filter(n -> !(n instanceof NotNode)).toList());
            final List<Node> nots = new ArrayList<>(
                    and.nodes().stream().filter(n -> n instanceof NotNode).toList());
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
                tmp.add(new ZeroNode());

                if (tmp.size() == 1) {
                    return Optional.of(new OptimizationResult(score, new ZeroNode()));
                }
                return Optional.of(new OptimizationResult(score + 1, new AndNode(tmp)));
            }
        }

        return Optional.empty();
    }
}

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
package com.ledmington.ast;

import java.util.List;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.BracketsNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.ZeroNode;

public final class Optimizer {

    private Optimizer() {}

    public static Node optimize(final Node root) {
        if (root instanceof BracketsNode br) {
            return optimize(br.inner());
        }

        if (root instanceof NotNode not) {
            if (not.inner() instanceof ZeroNode) {
                return new OneNode();
            }
            if (not.inner() instanceof OneNode) {
                return new ZeroNode();
            }
            return optimize(not.inner());
        }

        if (root instanceof AndNode and) {
            if (and.nodes().contains(new ZeroNode())) {
                return new ZeroNode();
            }
            if (and.nodes().contains(new OneNode())) {
                final List<Node> tmp = and.nodes().stream()
                        .filter(n -> !(n instanceof OneNode))
                        .toList();
                if (tmp.size() < 2) {
                    return tmp.get(0);
                } else {
                    return new AndNode(tmp);
                }
            }
            return new AndNode(and.nodes().stream().map(Optimizer::optimize).toList());
        }

        if (root instanceof OrNode or) {
            if (or.nodes().contains(new ZeroNode())) {
                final List<Node> tmp = or.nodes().stream()
                        .filter(n -> !(n instanceof ZeroNode))
                        .toList();
                if (tmp.size() < 2) {
                    return tmp.get(0);
                } else {
                    return new OrNode(tmp);
                }
            }
            if (or.nodes().contains(new OneNode())) {
                return new OneNode();
            }
            return new OrNode(or.nodes().stream().map(Optimizer::optimize).toList());
        }

        return root;
    }
}

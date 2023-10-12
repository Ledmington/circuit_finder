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

import java.util.ArrayList;
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
        Node current;
        Node next = root;

        do {
            current = next;
            next = optimizeActual(current);
        } while (next.size() < current.size());

        return current;
    }

    private static Node optimizeActual(final Node root) {
        if (root instanceof BracketsNode br) {
            return optimizeActual(br.inner());
        }

        if (root instanceof NotNode not) {
            if (not.inner() instanceof ZeroNode) {
                return new OneNode();
            }
            if (not.inner() instanceof OneNode) {
                return new ZeroNode();
            }
            if (not.inner() instanceof NotNode notnot) {
                return optimizeActual(notnot.inner());
            }
            return new NotNode(optimizeActual(not.inner()));
        }

        if (root instanceof AndNode and) {
            if (and.nodes().contains(new ZeroNode())) {
                return new ZeroNode();
            }
            if (and.nodes().contains(new OneNode())) {
                final List<Node> tmp = and.nodes().stream()
                        .filter(n -> !(n instanceof OneNode))
                        .toList();
                if (tmp.isEmpty()) {
                    return new OneNode();
                }
                if (tmp.size() == 1) {
                    return tmp.get(0);
                }
                return new AndNode(tmp);
            }
            if (and.nodes().stream().anyMatch(n -> n instanceof AndNode)) {
                final List<Node> tmp = new ArrayList<>();
                for (final Node n : and.nodes()) {
                    if (n instanceof AndNode) {
                        tmp.addAll(((AndNode) n).nodes());
                    } else {
                        tmp.add(n);
                    }
                }
                return new AndNode(tmp.stream().map(Optimizer::optimizeActual).toList());
            }
            return new AndNode(
                    and.nodes().stream().map(Optimizer::optimizeActual).toList());
        }

        if (root instanceof OrNode or) {
            if (or.nodes().contains(new ZeroNode())) {
                final List<Node> tmp = or.nodes().stream()
                        .filter(n -> !(n instanceof ZeroNode))
                        .toList();
                if (tmp.isEmpty()) {
                    return new ZeroNode();
                }
                if (tmp.size() == 1) {
                    return tmp.get(0);
                }
                return new OrNode(tmp);
            }
            if (or.nodes().contains(new OneNode())) {
                return new OneNode();
            }
            if (or.nodes().stream().anyMatch(n -> n instanceof OrNode)) {
                final List<Node> tmp = new ArrayList<>();
                for (final Node n : or.nodes()) {
                    if (n instanceof OrNode) {
                        tmp.addAll(((OrNode) n).nodes());
                    } else {
                        tmp.add(n);
                    }
                }
                return new OrNode(tmp.stream().map(Optimizer::optimizeActual).toList());
            }
            return new OrNode(or.nodes().stream().map(Optimizer::optimizeActual).toList());
        }

        return root;
    }
}

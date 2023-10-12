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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            return optimizeNot(not);
        }

        if (root instanceof AndNode and) {
            return optimizeAnd(and);
        }

        if (root instanceof OrNode or) {
            return optimizeOr(or);
        }

        // variables, 0s and 1s are returned as they are.
        return root;
    }

    private static Node optimizeNot(final NotNode not) {
        // ~0 = 1
        if (not.inner() instanceof ZeroNode) {
            return new OneNode();
        }

        // ~1 = 0
        if (not.inner() instanceof OneNode) {
            return new ZeroNode();
        }

        // ~~X = X
        if (not.inner() instanceof NotNode notnot) {
            return optimizeActual(notnot.inner());
        }

        return new NotNode(optimizeActual(not.inner()));
    }

    private static Node optimizeAnd(final AndNode and) {
        // A & 0 = 0
        if (and.nodes().contains(new ZeroNode())) {
            return new ZeroNode();
        }

        // A & 1 = A
        if (and.nodes().contains(new OneNode())) {
            final List<Node> tmp =
                    and.nodes().stream().filter(n -> !(n instanceof OneNode)).toList();
            if (tmp.isEmpty()) {
                return new OneNode();
            }
            if (tmp.size() == 1) {
                return tmp.get(0);
            }
            return new AndNode(tmp);
        }

        // A & (B & C) = A & B & C
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

        // A & A & B = A & B
        if (new HashSet<>(and.nodes()).size() < and.nodes().size()) {
            final Set<Node> uniques = new HashSet<>(and.nodes());
            if (uniques.isEmpty()) {
                return new OneNode();
            }
            if (uniques.size() == 1) {
                return optimizeActual(uniques.iterator().next());
            }
            return new AndNode(uniques.stream().map(Optimizer::optimizeActual).toList());
        }

        // A & (A + B) = A
        if (and.nodes().stream().anyMatch(n -> n instanceof OrNode)) {
            final boolean[] tobeAdded = new boolean[and.nodes().size()];
            Arrays.fill(tobeAdded, true);
            for (int i = 0; i < and.nodes().size(); i++) {
                final Node a = and.nodes().get(i);
                for (int j = i + 1; j < and.nodes().size(); j++) {
                    final Node b = and.nodes().get(j);
                    if (!(b instanceof OrNode)) {
                        continue;
                    }
                    if (((OrNode) b).nodes().contains(a)) {
                        tobeAdded[j] = false;
                    }
                }
            }
            final List<Node> tmp = new ArrayList<>();
            for (int i = 0; i < tobeAdded.length; i++) {
                if (tobeAdded[i]) {
                    tmp.add(and.nodes().get(i));
                }
            }
            if (tmp.isEmpty()) {
                return new OneNode();
            }
            if (tmp.size() == 1) {
                return optimizeActual(tmp.get(0));
            }
            return new AndNode(tmp.stream().map(Optimizer::optimizeActual).toList());
        }

        return new AndNode(and.nodes().stream().map(Optimizer::optimizeActual).toList());
    }

    private static Node optimizeOr(final OrNode or) {
        // X + 0 = X
        if (or.nodes().contains(new ZeroNode())) {
            final List<Node> tmp =
                    or.nodes().stream().filter(n -> !(n instanceof ZeroNode)).toList();
            if (tmp.isEmpty()) {
                return new ZeroNode();
            }
            if (tmp.size() == 1) {
                return tmp.get(0);
            }
            return new OrNode(tmp);
        }

        // X + 1 = 1
        if (or.nodes().contains(new OneNode())) {
            return new OneNode();
        }

        // A + (B + C) = A + B + C
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

        // A + A + B = A + B
        if (new HashSet<>(or.nodes()).size() < or.nodes().size()) {
            final Set<Node> uniques = new HashSet<>(or.nodes());
            if (uniques.isEmpty()) {
                return new ZeroNode();
            }
            if (uniques.size() == 1) {
                return optimizeActual(uniques.iterator().next());
            }
            return new OrNode(uniques.stream().map(Optimizer::optimizeActual).toList());
        }

        // A & (A + B) = A
        if (or.nodes().stream().anyMatch(n -> n instanceof AndNode)) {
            final boolean[] tobeAdded = new boolean[or.nodes().size()];
            Arrays.fill(tobeAdded, true);
            for (int i = 0; i < or.nodes().size(); i++) {
                final Node a = or.nodes().get(i);
                for (int j = i + 1; j < or.nodes().size(); j++) {
                    final Node b = or.nodes().get(j);
                    if (!(b instanceof AndNode)) {
                        continue;
                    }
                    if (((AndNode) b).nodes().contains(a)) {
                        tobeAdded[j] = false;
                    }
                }
            }
            final List<Node> tmp = new ArrayList<>();
            for (int i = 0; i < tobeAdded.length; i++) {
                if (tobeAdded[i]) {
                    tmp.add(or.nodes().get(i));
                }
            }
            if (tmp.isEmpty()) {
                return new ZeroNode();
            }
            if (tmp.size() == 1) {
                return optimizeActual(tmp.get(0));
            }
            return new OrNode(tmp.stream().map(Optimizer::optimizeActual).toList());
        }

        return new OrNode(or.nodes().stream().map(Optimizer::optimizeActual).toList());
    }
}

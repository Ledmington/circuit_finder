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
import java.util.function.Function;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.BracketsNode;
import com.ledmington.ast.nodes.MultiNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.ZeroNode;
import com.ledmington.utils.MiniLogger;

public final class Optimizer {

    private static final MiniLogger logger = MiniLogger.getLogger("optimizer");

    private Optimizer() {}

    public static Node optimize(final Node root) {
        final int initialSize = root.size();
        Node current = root;
        Node next = optimizeActual(current);

        logger.info("Initial AST size %,d", initialSize);

        for (int i = 0; next.size() < current.size(); i++) {
            logger.info(
                    "Optimization n. %,d: reduced AST size from %,d to %,d (%5.2f%%)",
                    i, current.size(), next.size(), (double) next.size() / (double) current.size() * 100.0);
            current = next;
            next = optimizeActual(current);
        }

        logger.info(
                "Final AST size %,d (%5.2f%% of original size)",
                current.size(), (double) current.size() / (double) initialSize * 100.0);

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
            logger.debug("Optimizing '%s' to '1'", not);
            return new OneNode();
        }

        // ~1 = 0
        if (not.inner() instanceof OneNode) {
            logger.debug("Optimizing '%s' to '0'", not);
            return new ZeroNode();
        }

        // ~~X = X
        if (not.inner() instanceof NotNode notnot) {
            logger.debug("Optimizing double not '%s' to '%s'", not, notnot.inner());
            return optimizeActual(notnot.inner());
        }

        return new NotNode(optimizeActual(not.inner()));
    }

    /**
     * Generic method which works both for AND and for OR.
     *
     * @param root
     *      The root of the tree to be optimized.
     * @param opConstructor
     *      A constructor for the given operation.
     * @param inverseConstructor
     *      A constructor for the "inverse" of the given operation.
     * @param identity
     *      The identity element for the given operation (1 for AND, 0 for OR)
     * @param annihilator
     *      The annihilator element for the given operation (0 for AND, 1 for OR)
     */
    private static Node optimizeGeneric(
            final MultiNode root,
            final Function<List<Node>, Node> opConstructor,
            final Function<List<Node>, Node> inverseConstructor,
            final Node identity,
            final Node annihilator) {

        // A & 0 = 0
        // A + 1 = 1
        if (root.nodes().contains(annihilator)) {
            logger.debug("Optimizing annihilator '%s' to '%s'", root, annihilator);
            return annihilator;
        }

        // A & 1 = A
        // A + 0 = A
        if (root.nodes().contains(identity)) {
            final List<Node> tmp =
                    root.nodes().stream().filter(n -> !n.equals(identity)).toList();
            if (tmp.isEmpty()) {
                logger.debug("Optimizing identity '%s' to '%s'", root, identity);
                return identity;
            }
            if (tmp.size() == 1) {
                logger.debug("Optimizing identity '%s' to '%s'", root, tmp.get(0));
                return optimizeActual(tmp.get(0));
            }
            final Node toReturn = opConstructor.apply(
                    tmp.stream().map(Optimizer::optimizeActual).toList());
            logger.debug("Optimizing identity '%s' to '%s'", root, toReturn);
            return toReturn;
        }

        final Class<?> rootClass = (root instanceof AndNode) ? AndNode.class : OrNode.class;
        final Class<?> inverseClass = (root instanceof AndNode) ? OrNode.class : AndNode.class;

        // A & (B & C) = A & B & C
        // A + (B + C) = A + B + C
        if (root.nodes().stream().anyMatch(n -> n.getClass().equals(rootClass))) {
            final List<Node> tmp = new ArrayList<>();
            for (final Node n : root.nodes()) {
                if (n.getClass().equals(rootClass)) {
                    tmp.addAll(((MultiNode) n).nodes());
                } else {
                    tmp.add(n);
                }
            }
            return opConstructor.apply(
                    tmp.stream().map(Optimizer::optimizeActual).toList());
        }

        // A & A & B = A & B
        // A + A + B = A + B
        if (new HashSet<>(root.nodes()).size() < root.nodes().size()) {
            final Set<Node> uniques = new HashSet<>(root.nodes());
            if (uniques.size() == 1) {
                logger.debug(
                        "Optimizing repetition '%s' to '%s'",
                        root, uniques.iterator().next());
                return optimizeActual(uniques.iterator().next());
            }
            final Node toReturn = opConstructor.apply(
                    uniques.stream().map(Optimizer::optimizeActual).toList());
            logger.debug("Optimizing repetition '%s' to '%s'", root, toReturn);
            return toReturn;
        }

        // A & (A + B) = A
        // A + (A & B) = A
        if (root.nodes().stream().anyMatch(n -> n.getClass().equals(inverseClass))) {
            final boolean[] tobeAdded = new boolean[root.nodes().size()];
            Arrays.fill(tobeAdded, true);
            for (int i = 0; i < root.nodes().size(); i++) {
                final Node a = root.nodes().get(i);
                if (!tobeAdded[i]) {
                    continue;
                }
                for (int j = i + 1; j < root.nodes().size(); j++) {
                    final Node b = root.nodes().get(j);
                    if (!b.getClass().equals(inverseClass)) {
                        continue;
                    }
                    if (((MultiNode) b).nodes().contains(a)) {
                        tobeAdded[j] = false;
                    }
                }
            }
            final List<Node> tmp = new ArrayList<>();
            for (int i = 0; i < tobeAdded.length; i++) {
                if (tobeAdded[i]) {
                    tmp.add(root.nodes().get(i));
                }
            }
            if (tmp.isEmpty()) {
                logger.debug("Optimizing absorption '%s' to '%s'", root, identity);
                return identity;
            }
            if (tmp.size() == 1) {
                logger.debug("Optimizing absorption '%s' to '%s'", root, tmp.get(0));
                return optimizeActual(tmp.get(0));
            }
            final Node toReturn = opConstructor.apply(
                    tmp.stream().map(Optimizer::optimizeActual).toList());
            logger.debug("Optimizing absorption '%s' to '%s'", root, toReturn);
            return toReturn;
        }

        // (A & B) + (A & C) = A & (B + C)
        // (A + B) & (A + C) = A + (B & C)
        // TODO
        /*if(root.nodes().stream().filter(n->n.getClass().equals(inverseClass)).count()>=2) {
        final List<Node> tmp= root.nodes().stream().filter(n->n.getClass().equals(inverseClass)).toList();
        for(int i=0;i<tmp.size();i++){
            final MultiNode a= (MultiNode) tmp.get(i);
            for(int j=i+1;j<tmp.size();j++){
                final MultiNode b=(MultiNode)tmp.get(j);
                final
                for(final Node n:a.nodes()){
                    if(b.nodes().contains(n)){
                        // n is repeated

                    }
                }
            }
        }
                }*/

        // default behavior: continue exploring down
        return opConstructor.apply(
                root.nodes().stream().map(Optimizer::optimizeActual).toList());
    }

    private static Node optimizeAnd(final AndNode and) {
        return optimizeGeneric(and, AndNode::new, OrNode::new, new OneNode(), new ZeroNode());
    }

    private static Node optimizeOr(final OrNode or) {
        return optimizeGeneric(or, OrNode::new, AndNode::new, new ZeroNode(), new OneNode());
    }
}

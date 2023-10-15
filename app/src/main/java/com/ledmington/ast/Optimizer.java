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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.BracketsNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.VariableNode;
import com.ledmington.ast.nodes.ZeroNode;
import com.ledmington.ast.opt.AndComplementation;
import com.ledmington.ast.opt.AndIdempotence;
import com.ledmington.ast.opt.AndOne;
import com.ledmington.ast.opt.AndZero;
import com.ledmington.ast.opt.DoubleNot;
import com.ledmington.ast.opt.MergeAnd;
import com.ledmington.ast.opt.MergeOr;
import com.ledmington.ast.opt.NoBrackets;
import com.ledmington.ast.opt.NotConstant;
import com.ledmington.ast.opt.Optimization;
import com.ledmington.ast.opt.OptimizationResult;
import com.ledmington.ast.opt.OrComplementation;
import com.ledmington.ast.opt.OrIdempotence;
import com.ledmington.ast.opt.OrOne;
import com.ledmington.ast.opt.OrZero;
import com.ledmington.utils.ImmutableSet;
import com.ledmington.utils.MiniLogger;

public final class Optimizer {

    private static final MiniLogger logger = MiniLogger.getLogger("optimizer");
    private static final Set<Optimization> optimizations = ImmutableSet.<Optimization>builder()
            .add(new NotConstant())
            .add(new DoubleNot())
            .add(new NoBrackets())
            .add(new OrOne())
            .add(new AndZero())
            .add(new OrZero())
            .add(new AndOne())
            .add(new OrComplementation())
            .add(new AndComplementation())
            .add(new AndIdempotence())
            .add(new OrIdempotence())
            .add(new MergeAnd())
            .add(new MergeOr())
            .build();
    private final int maxDepth;

    /**
     * Creates a new Optimizer with the given max optimization depth.
     * A lower maxDepth value provides better performance but may lead
     * to less optimized results.
     *
     * @param maxDepth
     *      Maximum optimization search depth.
     */
    public Optimizer(int maxDepth) {
        if (maxDepth < 1) {
            throw new IllegalArgumentException(
                    String.format("Invalid maximum optimization depth: should have been >=1 but was %,d", maxDepth));
        }
        this.maxDepth = maxDepth;
    }

    public Node optimize(final Node root) {
        Objects.requireNonNull(root);

        // TODO: remove this warning when finished
        logger.warning("The Optimizer class is currently the core of a heavy rework.");

        final int initialSize = root.size();
        Node current = root;
        Node next = optimizeIteration(root);

        for (int i = 1; next.size() < current.size(); i++) {
            logger.debug("Optimization iteration %,d: reduced size from %,d to %,d", i, current.size(), next.size());
            current = next;
            next = optimizeIteration(next);
        }

        logger.info(
                "Final AST size %,d (%5.2f%% of initial size)",
                next.size(), (double) next.size() / (double) initialSize * 100.0);

        return next;
    }

    /**
     * This method selects one and only one optimization and applies it.
     */
    private Node optimizeIteration(final Node root) {
        int bestScore = Integer.MAX_VALUE;
        Node whereToApply = null;
        Node bestResult = null;

        final List<Node> allNodes = getStream(root).toList();
        for (final Node n : allNodes) {
            for (final Optimization opt : optimizations) {
                final Optional<OptimizationResult> r = opt.check(n);
                if (r.isPresent() && r.orElseThrow().score() < bestScore) {
                    bestScore = r.orElseThrow().score();
                    whereToApply = n;
                    bestResult = r.orElseThrow().result();
                }
            }
        }

        // no optimization was appliable
        if (bestResult == null) {
            return root;
        }

        logger.debug("Applying optimization from '%s' to '%s' with score %,d", whereToApply, bestResult, bestScore);

        return applyOptimization(root, whereToApply, bestResult);
    }

    private Node applyOptimization(final Node astRoot, final Node optimizationRoot, final Node optimizedAST) {
        Objects.requireNonNull(astRoot);
        Objects.requireNonNull(optimizationRoot);
        Objects.requireNonNull(optimizedAST);

        if (astRoot.equals(optimizationRoot)) {
            return optimizedAST;
        }

        if (astRoot instanceof BracketsNode br) {
            if (br.inner().equals(optimizationRoot)) {
                return new BracketsNode(optimizedAST);
            }
            return applyOptimization(br.inner(), optimizationRoot, optimizedAST);
        }

        if (astRoot instanceof NotNode not) {
            if (not.inner().equals(optimizationRoot)) {
                return new NotNode(optimizedAST);
            }
            return applyOptimization(not.inner(), optimizationRoot, optimizedAST);
        }

        if (astRoot instanceof AndNode and) {
            if (and.nodes().contains(optimizationRoot)) {
                final List<Node> tmp = new ArrayList<>(and.nodes());
                tmp.remove(optimizationRoot);
                tmp.add(optimizedAST);
                return new AndNode(tmp);
            }
            // currently the optimization goes on exploring all the nodes in the AST even if not needed
            return new AndNode(and.nodes().stream()
                    .map(n -> applyOptimization(n, optimizationRoot, optimizedAST))
                    .toList());
        }

        if (astRoot instanceof OrNode or) {
            if (or.nodes().contains(optimizationRoot)) {
                final List<Node> tmp = new ArrayList<>(or.nodes());
                tmp.remove(optimizationRoot);
                tmp.add(optimizedAST);
                return new OrNode(tmp);
            }
            // currently the optimization goes on exploring all the nodes in the AST even if not needed
            return new OrNode(or.nodes().stream()
                    .map(n -> applyOptimization(n, optimizationRoot, optimizedAST))
                    .toList());
        }

        // variables, 0s and 1s are returned as they are
        return astRoot;
    }

    private Stream<Node> getStream(final Node root) {
        if (root instanceof ZeroNode || root instanceof OneNode || root instanceof VariableNode) {
            return Stream.of(root);
        }

        if (root instanceof BracketsNode br) {
            return Stream.concat(Stream.of(root), getStream(br.inner()));
        }
        if (root instanceof NotNode not) {
            return Stream.concat(Stream.of(root), getStream(not.inner()));
        }

        if (root instanceof AndNode and) {
            return Stream.concat(Stream.of(root), and.nodes().stream().flatMap(this::getStream));
        }
        if (root instanceof OrNode or) {
            return Stream.concat(Stream.of(root), or.nodes().stream().flatMap(this::getStream));
        }

        throw new IllegalArgumentException(String.format("Unknown Node type '%s'", root));
    }
}

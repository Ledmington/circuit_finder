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

import java.util.Optional;
import java.util.Set;

import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.opt.AndOne;
import com.ledmington.ast.opt.AndZero;
import com.ledmington.ast.opt.DoubleNot;
import com.ledmington.ast.opt.NoBrackets;
import com.ledmington.ast.opt.NotConstant;
import com.ledmington.ast.opt.Optimization;
import com.ledmington.ast.opt.OptimizationResult;
import com.ledmington.ast.opt.OrComplementation;
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
        // TODO: remove this warning when finished
        logger.warning("The Optimizer class is currently the core of a heavy rework.");

        int bestScore = Integer.MAX_VALUE;
        Node bestResult = null;

        for (final Optimization opt : optimizations) {
            final Optional<OptimizationResult> r = opt.check(root);
            if (r.isPresent() && r.orElseThrow().score() < bestScore) {
                bestScore = r.orElseThrow().score();
                bestResult = r.orElseThrow().result();
            }
        }

        // no optimization was appliable
        if (bestResult == null) {
            return root;
        }

        return bestResult;
    }
}

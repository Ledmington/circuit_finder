/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

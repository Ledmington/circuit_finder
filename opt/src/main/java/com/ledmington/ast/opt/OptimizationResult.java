/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

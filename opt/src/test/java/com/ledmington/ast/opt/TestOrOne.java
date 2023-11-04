/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.opt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import com.ledmington.ast.TestOptimizer;
import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class TestOrOne extends TestOptimizer {

    private final Optimization opt = new OrOne();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(one(), and(zero(), one()), or(A(), B()), zero()).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    public void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(or(zero(), one()), -2, one()),
                Arguments.of(or(A(), one()), -2, one()),
                Arguments.of(or(A(), B(), one()), -3, one()),
                Arguments.of(or(A(), one(), B()), -3, one()),
                Arguments.of(or(one(), A(), B()), -3, one()));
    }

    @ParameterizedTest
    @MethodSource("validCases")
    public void validCases(final Node before, int score, final Node expected) {
        final Optional<OptimizationResult> r = opt.check(before);
        assertTrue(r.isPresent());
        assertEquals(score, r.orElseThrow().score());
        assertEquals(expected, r.orElseThrow().result());
    }
}

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

public final class TestOrComplementation extends TestOptimizer {

    private final Optimization opt = new OrComplementation();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(zero(), one(), A(), and(A(), B()), or(A(), B()), or(A(), not(B())))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    public void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(or(A(), not(A())), -3, one()),
                Arguments.of(or(B(), A(), not(A())), -2, or(B(), one())),
                Arguments.of(or(A(), B(), not(A())), -2, or(B(), one())),
                Arguments.of(or(A(), not(A()), B()), -2, or(B(), one())),
                Arguments.of(or(A(), not(A()), not(A())), -2, or(not(A()), one())),
                Arguments.of(or(A(), A(), not(A())), -2, or(A(), one())),
                Arguments.of(or(A(), not(A()), B(), not(B()), C()), -5, or(C(), one())));
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

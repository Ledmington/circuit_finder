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

public final class TestAndAbsorption extends TestOptimizer {

    private final Optimization opt = new AndAbsorption();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(zero(), one(), A(), or(A(), B()), and(A(), B()), and(A(), not(B())), and(A(), or(B(), C())))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    public void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(and(A(), or(A(), B())), -4, A()),
                Arguments.of(and(A(), or(B(), A())), -4, A()),
                Arguments.of(and(or(A(), B()), A()), -4, A()),
                Arguments.of(and(or(B(), A()), A()), -4, A()),
                Arguments.of(and(not(A()), or(not(A()), B())), -5, not(A())),
                Arguments.of(and(not(A()), or(B(), not(A()))), -5, not(A())),
                Arguments.of(and(or(not(A()), B()), not(A())), -5, not(A())),
                Arguments.of(and(or(B(), not(A())), not(A())), -5, not(A())),
                Arguments.of(and(and(A(), B()), or(and(A(), B()), C())), -6, and(A(), B())),
                Arguments.of(and(A(), B(), or(A(), C())), -3, and(A(), B())),
                Arguments.of(and(or(A(), B()), or(or(A(), B()), C())), -6, or(A(), B())));
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

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

public final class TestOrAbsorption extends TestOptimizer {

    private final Optimization opt = new OrAbsorption();

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
                Arguments.of(or(A(), and(A(), B())), -4, A()),
                Arguments.of(or(A(), and(B(), A())), -4, A()),
                Arguments.of(or(and(A(), B()), A()), -4, A()),
                Arguments.of(or(and(B(), A()), A()), -4, A()),
                Arguments.of(or(not(A()), and(not(A()), B())), -5, not(A())),
                Arguments.of(or(not(A()), and(B(), not(A()))), -5, not(A())),
                Arguments.of(or(and(not(A()), B()), not(A())), -5, not(A())),
                Arguments.of(or(and(B(), not(A())), not(A())), -5, not(A())),
                Arguments.of(or(or(A(), B()), and(or(A(), B()), C())), -6, or(A(), B())),
                Arguments.of(or(A(), B(), and(A(), C())), -3, or(A(), B())),
                Arguments.of(or(and(A(), B()), and(and(A(), B()), C())), -6, and(A(), B())));
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

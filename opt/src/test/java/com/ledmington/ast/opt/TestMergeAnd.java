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

public final class TestMergeAnd extends TestOptimizer {

    private final Optimization opt = new MergeAnd();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(zero(), one(), A(), and(A(), A()), or(A(), B()), or(A(), not(A())))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    public void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(and(A(), and(B(), C())), -1, and(A(), B(), C())),
                Arguments.of(and(B(), and(A(), C())), -1, and(A(), B(), C())),
                Arguments.of(and(C(), and(B(), A())), -1, and(A(), B(), C())),
                Arguments.of(and(and(A(), B()), and(C(), D())), -2, and(A(), B(), C(), D())));
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

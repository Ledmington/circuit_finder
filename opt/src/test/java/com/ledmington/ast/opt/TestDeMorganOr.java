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

import java.util.Optional;
import java.util.stream.Stream;

import com.ledmington.ast.TestOptimizer;
import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestDeMorganOr extends TestOptimizer {

    private final Optimization opt = new DeMorganOr();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(zero(), one(), A(), or(A(), B()), and(A(), B()), and(A(), not(B())), and(A(), or(B(), C())))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(not(or(not(A()), not(B()))), -3, and(A(), B())),
                Arguments.of(not(or(A(), not(B()))), -1, and(not(A()), B())),
                Arguments.of(not(or(not(A()), B())), -1, and(A(), not(B()))),
                Arguments.of(or(not(A()), not(B())), -1, not(and(A(), B()))),
                Arguments.of(not(or(not(A()), not(B()), not(C()))), -4, and(A(), B(), C())),
                Arguments.of(not(or(A(), not(B()), not(C()))), -2, and(not(A()), B(), C())),
                Arguments.of(not(or(not(A()), B(), not(C()))), -2, and(A(), not(B()), C())),
                Arguments.of(not(or(not(A()), not(B()), C())), -2, and(A(), B(), not(C()))));
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void validCases(final Node before, int score, final Node expectedAST) {
        final Optional<OptimizationResult> r = opt.check(before);
        final OptimizationResult expected = new OptimizationResult(score, expectedAST);
        assertEquals(r.orElseThrow(), expected);
    }
}

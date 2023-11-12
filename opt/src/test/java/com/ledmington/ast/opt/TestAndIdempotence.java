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

final class TestAndIdempotence extends TestOptimizer {

    private final Optimization opt = new AndIdempotence();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(zero(), one(), A(), or(A(), B()), and(A(), B()), and(A(), not(A())))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(and(A(), A()), -2, A()),
                Arguments.of(and(A(), A(), A()), -3, A()),
                Arguments.of(and(A(), A(), A(), A()), -4, A()),
                Arguments.of(and(A(), A(), B()), -1, and(A(), B())),
                Arguments.of(and(A(), B(), A()), -1, and(A(), B())),
                Arguments.of(and(B(), A(), A()), -1, and(A(), B())),
                Arguments.of(and(A(), A(), B(), B()), -2, and(A(), B())));
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void validCases(final Node before, int score, final Node expectedAST) {
        final Optional<OptimizationResult> r = opt.check(before);
        final OptimizationResult expected = new OptimizationResult(score, expectedAST);
        assertEquals(r.orElseThrow(), expected);
    }
}

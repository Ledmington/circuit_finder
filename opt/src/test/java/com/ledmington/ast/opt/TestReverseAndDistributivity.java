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

public final class TestReverseAndDistributivity extends TestOptimizer {

    private final Optimization opt = new ReverseAndDistributivity();

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
                Arguments.of(or(and(A(), B()), and(A(), C())), -2, and(A(), or(B(), C()))),
                Arguments.of(or(and(A(), B()), and(A(), C(), D())), -1, and(A(), or(B(), and(C(), D())))),
                Arguments.of(or(and(A(), B()), and(A(), or(C(), D()))), -2, and(A(), or(B(), or(C(), D())))),
                Arguments.of(or(and(A(), B(), C()), and(A(), B(), D())), 0, and(A(), or(and(B(), C()), and(B(), D())))),
                Arguments.of(or(and(A(), B()), and(A(), C()), and(A(), D())), -4, and(A(), or(B(), C(), D()))),
                Arguments.of(
                        or(and(A(), B()), and(B(), C()), and(C(), D())), -1, or(and(B(), or(A(), C())), and(C(), D()))),
                Arguments.of(or(and(A(), B()), and(A(), C()), D()), -1, or(and(A(), or(B(), C())), D())),
                Arguments.of(or(A(), and(B(), C()), and(C(), D()), E()), -1, or(A(), and(C(), or(B(), D())), E())),
                Arguments.of(
                        or(and(A(), B()), and(A(), C(), D()), and(A(), C(), E())),
                        -2,
                        and(A(), or(B(), and(C(), D()), and(C(), E())))));
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

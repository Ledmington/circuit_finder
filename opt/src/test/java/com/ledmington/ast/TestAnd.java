/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestAnd extends TestOptimizer {

    private static Stream<Arguments> andProperties() {
        return Stream.of(
                Arguments.of(and(A(), A()), A()),
                Arguments.of(and(A(), A(), B()), and(A(), B())),
                Arguments.of(and(A(), B()), and(A(), B())),
                Arguments.of(and(and(A(), B()), C()), and(A(), B(), C())),
                Arguments.of(and(A(), and(B(), C())), and(A(), B(), C())));
    }

    @ParameterizedTest
    @MethodSource("andProperties")
    void andProperties(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }

    @Test
    void stringRepresentation() {
        assertEquals("A&(B+C)", and(A(), or(B(), C())).toString());
    }
}

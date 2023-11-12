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

final class TestNot extends TestOptimizer {

    private static Stream<Arguments> notProperties() {
        return Stream.of(
                Arguments.of(A(), A()),
                Arguments.of(not(A()), not(A())),
                Arguments.of(not(not(A())), A()),
                Arguments.of(not(not(not(A()))), not(A())),
                Arguments.of(not(not(not(not(A())))), A()),
                Arguments.of(not(not(not(not(not(A()))))), not(A())),
                Arguments.of(not(not(not(not(not(not(A())))))), A()),
                Arguments.of(not(not(not(not(not(not(not(A()))))))), not(A())));
    }

    @ParameterizedTest
    @MethodSource("notProperties")
    void notProperties(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }

    @Test
    void stringRepresentation() {
        assertEquals("~A", not(A()).toString());
    }
}

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

public class TestOr extends TestOptimizer {

    private static Stream<Arguments> orProperties() {
        return Stream.of(
                Arguments.of(or(A(), A()), A()),
                Arguments.of(or(A(), A(), B()), or(A(), B())),
                Arguments.of(or(A(), B()), or(A(), B())),
                Arguments.of(or(or(A(), B()), C()), or(A(), B(), C())),
                Arguments.of(or(A(), or(B(), C())), or(A(), B(), C())));
    }

    @ParameterizedTest
    @MethodSource("orProperties")
    public void orProperties(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }

    @Test
    public void stringRepresentation() {
        assertEquals("A+(B&C)", or(A(), and(B(), C())).toString());
    }
}

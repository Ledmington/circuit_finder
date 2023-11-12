/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestNodes {

    private static Executable wrap(final Runnable task) {
        return task::run;
    }

    private static Stream<Arguments> invalidCases() {
        return Stream.of(
                Arguments.of(NullPointerException.class, wrap(() -> new NotNode(null))),
                Arguments.of(NullPointerException.class, wrap(() -> new AndNode(null))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new AndNode(List.of()))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new AndNode(List.of(new ZeroNode())))),
                Arguments.of(NullPointerException.class, wrap(() -> new OrNode(null))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new OrNode(List.of()))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new OrNode(List.of(new ZeroNode())))),
                Arguments.of(NullPointerException.class, wrap(() -> new VariableNode(null))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new VariableNode(""))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new VariableNode(" "))));
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void expectThrow(final Class<Throwable> exceptionClass, final Executable task) {
        assertThrows(exceptionClass, task);
    }

    @Test
    void andEqualsIgnoreOrder() {
        assertEquals(
                new AndNode(List.of(new ZeroNode(), new OneNode())),
                new AndNode(List.of(new OneNode(), new ZeroNode())));
    }

    @Test
    void orEqualsIgnoreOrder() {
        assertEquals(
                new OrNode(List.of(new ZeroNode(), new OneNode())), new OrNode(List.of(new OneNode(), new ZeroNode())));
    }
}

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.stream.Stream;

import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class TestEvaluate extends TestOptimizer {

    private static Stream<Arguments> allNodeTypes() {
        return Stream.of(zero(), one(), A(), not(A()), and(A(), B()), or(A(), B()))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("allNodeTypes")
    public void invalidInput(final Node n) {
        assertThrows(NullPointerException.class, () -> n.evaluate(null));
    }

    @Test
    public void evalZero() {
        assertFalse(zero().evaluate(Map.of()));
    }

    @Test
    public void evalOne() {
        assertTrue(one().evaluate(Map.of()));
    }

    private static Stream<Arguments> evalVar() {
        return Stream.of(
                Arguments.of(A(), Map.of("A", true), true),
                Arguments.of(A(), Map.of("A", false), false),
                Arguments.of(A(), Map.of("A", true, "B", false), true));
    }

    @ParameterizedTest
    @MethodSource("evalVar")
    public void evalVar(final Node ast, final Map<String, Boolean> input, final boolean result) {
        assertEquals(result, ast.evaluate(input));
    }

    private static Stream<Arguments> evalNot() {
        return Stream.of(
                Arguments.of(not(A()), Map.of("A", true), false),
                Arguments.of(not(A()), Map.of("A", false), true),
                Arguments.of(not(A()), Map.of("A", true, "B", false), false),
                Arguments.of(not(zero()), Map.of(), true));
    }

    @ParameterizedTest
    @MethodSource("evalNot")
    public void evalNot(final Node ast, final Map<String, Boolean> input, final boolean result) {
        assertEquals(result, ast.evaluate(input));
    }

    private static Stream<Arguments> evalOr() {
        return Stream.of(
                Arguments.of(or(A(), B()), Map.of("A", false, "B", false), false),
                Arguments.of(or(A(), B()), Map.of("A", true, "B", false), true),
                Arguments.of(or(A(), B()), Map.of("A", false, "B", true), true),
                Arguments.of(or(A(), B()), Map.of("A", true, "B", true), true));
    }

    @ParameterizedTest
    @MethodSource("evalOr")
    public void evalOr(final Node ast, final Map<String, Boolean> input, final boolean result) {
        assertEquals(result, ast.evaluate(input));
    }

    private static Stream<Arguments> evalAnd() {
        return Stream.of(
                Arguments.of(and(A(), B()), Map.of("A", false, "B", false), false),
                Arguments.of(and(A(), B()), Map.of("A", true, "B", false), false),
                Arguments.of(and(A(), B()), Map.of("A", false, "B", true), false),
                Arguments.of(and(A(), B()), Map.of("A", true, "B", true), true));
    }

    @ParameterizedTest
    @MethodSource("evalAnd")
    public void evalAnd(final Node ast, final Map<String, Boolean> input, final boolean result) {
        assertEquals(result, ast.evaluate(input));
    }
}

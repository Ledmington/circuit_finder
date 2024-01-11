/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2024 Filippo Barbari <filippo.barbari@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
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

final class TestEvaluate extends TestOptimizer {

    private static Stream<Arguments> allNodeTypes() {
        return Stream.of(zero(), one(), A(), not(A()), and(A(), B()), or(A(), B()))
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("allNodeTypes")
    void invalidInput(final Node n) {
        assertThrows(NullPointerException.class, () -> n.evaluate(null));
    }

    @Test
    void evalZero() {
        assertFalse(zero().evaluate(Map.of()));
    }

    @Test
    void evalOne() {
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
    void evalVar(final Node ast, final Map<String, Boolean> input, final boolean result) {
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
    void evalNot(final Node ast, final Map<String, Boolean> input, final boolean result) {
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
    void evalOr(final Node ast, final Map<String, Boolean> input, final boolean result) {
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
    void evalAnd(final Node ast, final Map<String, Boolean> input, final boolean result) {
        assertEquals(result, ast.evaluate(input));
    }
}

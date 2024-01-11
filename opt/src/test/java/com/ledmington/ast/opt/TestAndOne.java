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

final class TestAndOne extends TestOptimizer {

    private final Optimization opt = new AndOne();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(one(), and(zero(), A()), or(A(), B()), zero()).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(and(zero(), one()), -2, zero()),
                Arguments.of(and(one(), one()), -2, one()),
                Arguments.of(and(A(), one()), -2, A()),
                Arguments.of(and(one(), A()), -2, A()),
                Arguments.of(and(A(), B(), one()), -1, and(A(), B())),
                Arguments.of(and(A(), one(), B()), -1, and(A(), B())),
                Arguments.of(and(one(), A(), B()), -1, and(A(), B())),
                Arguments.of(and(A(), B(), one(), one()), -2, and(A(), B())),
                Arguments.of(and(A(), one(), B(), one()), -2, and(A(), B())),
                Arguments.of(and(one(), A(), B(), one()), -2, and(A(), B())),
                Arguments.of(and(A(), one(), one(), B()), -2, and(A(), B())),
                Arguments.of(and(one(), A(), one(), B()), -2, and(A(), B())),
                Arguments.of(and(one(), one(), A(), B()), -2, and(A(), B())));
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void validCases(final Node before, int score, final Node expectedAST) {
        final Optional<OptimizationResult> r = opt.check(before);
        final OptimizationResult expected = new OptimizationResult(score, expectedAST);
        assertEquals(r.orElseThrow(), expected);
    }
}

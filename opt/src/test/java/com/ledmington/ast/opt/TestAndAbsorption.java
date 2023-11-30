/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
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

final class TestAndAbsorption extends TestOptimizer {

    private final Optimization opt = new AndAbsorption();

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
                Arguments.of(and(A(), or(A(), B())), -4, A()),
                Arguments.of(and(A(), or(B(), A())), -4, A()),
                Arguments.of(and(or(A(), B()), A()), -4, A()),
                Arguments.of(and(or(B(), A()), A()), -4, A()),
                Arguments.of(and(not(A()), or(not(A()), B())), -5, not(A())),
                Arguments.of(and(not(A()), or(B(), not(A()))), -5, not(A())),
                Arguments.of(and(or(not(A()), B()), not(A())), -5, not(A())),
                Arguments.of(and(or(B(), not(A())), not(A())), -5, not(A())),
                Arguments.of(and(and(A(), B()), or(and(A(), B()), C())), -6, and(A(), B())),
                Arguments.of(and(A(), B(), or(A(), C())), -3, and(A(), B())),
                Arguments.of(and(or(A(), B()), or(or(A(), B()), C())), -6, or(A(), B())));
    }

    @ParameterizedTest
    @MethodSource("validCases")
    void validCases(final Node before, int score, final Node expectedAST) {
        final Optional<OptimizationResult> r = opt.check(before);
        final OptimizationResult expected = new OptimizationResult(score, expectedAST);
        assertEquals(r.orElseThrow(), expected);
    }
}

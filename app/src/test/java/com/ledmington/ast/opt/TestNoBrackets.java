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
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

public final class TestNoBrackets extends TestOptimizer {

    private final Optimization opt = new NoBrackets();

    private static Stream<Arguments> invalidCases() {
        return Stream.of(zero(), one(), A(), not(A()), not(zero()), not(one())).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    public void invalidCases(final Node n) {
        assertFalse(opt.check(n).isPresent());
    }

    private static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(brackets(zero()), -1, zero()),
                Arguments.of(brackets(one()), -1, one()),
                Arguments.of(brackets(A()), -1, A()));
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
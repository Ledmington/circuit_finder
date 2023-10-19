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

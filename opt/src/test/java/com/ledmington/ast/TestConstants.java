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

import java.util.stream.Stream;

import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestConstants extends TestOptimizer {

    private static Stream<Arguments> zeroProperties() {
        return Stream.of(
                Arguments.of(zero(), zero()),
                Arguments.of(not(zero()), one()),
                Arguments.of(or(zero(), zero()), zero()),
                Arguments.of(or(A(), zero()), A()),
                Arguments.of(or(zero(), A()), A()),
                Arguments.of(and(zero(), zero()), zero()),
                Arguments.of(and(A(), zero()), zero()),
                Arguments.of(and(zero(), A()), zero()),
                Arguments.of(and(A(), not(A())), zero()),
                Arguments.of(and(B(), A(), not(A())), zero()),
                Arguments.of(and(A(), B(), not(A())), zero()),
                Arguments.of(and(A(), not(A()), B()), zero()));
    }

    @ParameterizedTest
    @MethodSource("zeroProperties")
    void zeroProperties(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }

    private static Stream<Arguments> oneProperties() {
        return Stream.of(
                Arguments.of(one(), one()),
                Arguments.of(not(one()), zero()),
                Arguments.of(or(one(), one()), one()),
                Arguments.of(or(A(), one()), one()),
                Arguments.of(or(one(), A()), one()),
                Arguments.of(and(one(), one()), one()),
                Arguments.of(and(A(), one()), A()),
                Arguments.of(and(one(), A()), A()),
                Arguments.of(or(A(), not(A())), one()),
                Arguments.of(or(B(), A(), not(A())), one()),
                Arguments.of(or(A(), B(), not(A())), one()),
                Arguments.of(or(A(), not(A()), B()), one()));
    }

    @ParameterizedTest
    @MethodSource("oneProperties")
    void oneProperties(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }

    @Test
    void stringRepresentation() {
        assertEquals("0+1", or(zero(), one()).toString());
    }
}

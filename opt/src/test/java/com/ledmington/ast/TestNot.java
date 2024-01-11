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

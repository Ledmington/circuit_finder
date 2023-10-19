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
package com.ledmington.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestAnd extends TestOptimizer {

    private static Stream<Arguments> andProperties() {
        return Stream.of(
                Arguments.of(and(A(), A()), A()),
                Arguments.of(and(A(), A(), B()), and(A(), B())),
                Arguments.of(and(A(), B()), and(A(), B())),
                Arguments.of(and(and(A(), B()), C()), and(A(), B(), C())),
                Arguments.of(and(A(), and(B(), C())), and(A(), B(), C())));
    }

    @ParameterizedTest
    @MethodSource("andProperties")
    public void andProperties(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }
}

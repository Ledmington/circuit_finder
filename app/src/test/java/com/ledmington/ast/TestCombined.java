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

public class TestCombined extends TestOptimizer {

    private static Stream<Arguments> booleanLaws() {
        return Stream.of(
                // absorption 1
                Arguments.of(and(A(), brackets(or(A(), B()))), A()),
                // absorption 2
                Arguments.of(or(A(), brackets(and(A(), B()))), A()),
                // reverse distributivity of AND over OR
                Arguments.of(or(and(A(), B()), and(A(), C())), and(A(), or(B(), C()))),
                // reverse distributivity of OR over AND
                Arguments.of(and(or(A(), B()), or(A(), C())), or(A(), and(B(), C()))),
                // De Morgan theorem from AND to OR
                // ~(~A & ~B) = A + B
                Arguments.of(not(brackets(and(not(A()), not(B())))), or(A(), B())),
                // ~(A & ~B) = ~A + B
                Arguments.of(not(brackets(and(A(), not(B())))), or(not(A()), B())),
                // ~(~A & B) = A + ~B
                Arguments.of(not(brackets(and(not(A()), B()))), or(A(), not(B()))),
                // ~A & ~B = ~(A + B)
                Arguments.of(and(not(A()), not(B())), not(or(A(), B()))),
                // De Morgan theorem from OR to AND
                // ~(~A + ~B) = A & B
                Arguments.of(not(brackets(or(not(A()), not(B())))), and(A(), B())),
                // ~(A + ~B) = ~A & B
                Arguments.of(not(brackets(or(A(), not(B())))), and(not(A()), B())),
                // ~(~A + B) = A & ~B
                Arguments.of(not(brackets(or(not(A()), B()))), and(A(), not(B()))),
                // ~A + ~B = ~(A & B)
                Arguments.of(or(not(A()), not(B())), not(and(A(), B()))));
    }

    @ParameterizedTest
    @MethodSource("booleanLaws")
    public void booleanLaws(final Node before, final Node expected) {
        final Node after = Optimizer.optimize(before);
        assertEquals(expected, after);
    }
}

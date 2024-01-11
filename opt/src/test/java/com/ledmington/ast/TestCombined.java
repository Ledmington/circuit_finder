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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestCombined extends TestOptimizer {

    private static Stream<Arguments> booleanLaws() {
        return Stream.of(
                // absorption 1
                Arguments.of(and(A(), or(A(), B())), A()),
                // absorption 2
                Arguments.of(or(A(), and(A(), B())), A()),
                // reverse distributivity of AND over OR
                Arguments.of(or(and(A(), B()), and(A(), C())), and(A(), or(B(), C()))),
                // reverse distributivity of OR over AND
                Arguments.of(and(or(A(), B()), or(A(), C())), or(A(), and(B(), C()))),
                // De Morgan theorem from AND to OR
                // ~(~A & ~B) = A + B
                Arguments.of(not(and(not(A()), not(B()))), or(A(), B())),
                // ~(A & ~B) = ~A + B
                Arguments.of(not(and(A(), not(B()))), or(not(A()), B())),
                // ~(~A & B) = A + ~B
                Arguments.of(not(and(not(A()), B())), or(A(), not(B()))),
                // ~A & ~B = ~(A + B)
                Arguments.of(and(not(A()), not(B())), not(or(A(), B()))),
                // De Morgan theorem from OR to AND
                // ~(~A + ~B) = A & B
                Arguments.of(not(or(not(A()), not(B()))), and(A(), B())),
                // ~(A + ~B) = ~A & B
                Arguments.of(not(or(A(), not(B()))), and(not(A()), B())),
                // ~(~A + B) = A & ~B
                Arguments.of(not(or(not(A()), B())), and(A(), not(B()))),
                // ~A + ~B = ~(A & B)
                Arguments.of(or(not(A()), not(B())), not(and(A(), B()))),

                // three-variables De Morgan theorem from AND to OR
                // ~(~A & ~B & ~C) = A + B + C
                Arguments.of(not(and(not(A()), not(B()), not(C()))), or(A(), B(), C())),
                // ~(A & ~B & ~C) = ~A + B + C
                Arguments.of(not(and(A(), not(B()), not(C()))), or(not(A()), B(), C())),
                // ~(~A & B & ~C) = A + ~B + C
                Arguments.of(not(and(not(A()), B(), not(C()))), or(A(), not(B()), C())),
                // ~(~A & ~B & C) = A + B + ~C
                Arguments.of(not(and(not(A()), not(B()), C())), or(A(), B(), not(C()))),
                // three-variables De Morgan theorem from OR to AND
                // ~(~A + ~B + ~C) = A & B & C
                Arguments.of(not(or(not(A()), not(B()), not(C()))), and(A(), B(), C())),
                // ~(A + ~B + ~C) = ~A & B & C
                Arguments.of(not(or(A(), not(B()), not(C()))), and(not(A()), B(), C())),
                // ~(~A + B + ~C) = A & ~B & C
                Arguments.of(not(or(not(A()), B(), not(C()))), and(A(), not(B()), C())),
                // ~(~A + ~B + C) = A & B & ~C
                Arguments.of(not(or(not(A()), not(B()), C())), and(A(), B(), not(C()))));
    }

    @ParameterizedTest
    @MethodSource("booleanLaws")
    void booleanLaws(final Node before, final Node expected) {
        final Node after = opt.optimize(before);
        assertEquals(expected, after);
    }
}

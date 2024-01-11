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
package com.ledmington.ast.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestNodes {

    private static Executable wrap(final Runnable task) {
        return task::run;
    }

    private static Stream<Arguments> invalidCases() {
        return Stream.of(
                Arguments.of(NullPointerException.class, wrap(() -> new NotNode(null))),
                Arguments.of(NullPointerException.class, wrap(() -> new AndNode(null))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new AndNode(List.of()))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new AndNode(List.of(new ZeroNode())))),
                Arguments.of(NullPointerException.class, wrap(() -> new OrNode(null))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new OrNode(List.of()))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new OrNode(List.of(new ZeroNode())))),
                Arguments.of(NullPointerException.class, wrap(() -> new VariableNode(null))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new VariableNode(""))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new VariableNode(" "))));
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void expectThrow(final Class<Throwable> exceptionClass, final Executable task) {
        assertThrows(exceptionClass, task);
    }

    @Test
    void andEqualsIgnoreOrder() {
        assertEquals(
                new AndNode(List.of(new ZeroNode(), new OneNode())),
                new AndNode(List.of(new OneNode(), new ZeroNode())));
    }

    @Test
    void orEqualsIgnoreOrder() {
        assertEquals(
                new OrNode(List.of(new ZeroNode(), new OneNode())), new OrNode(List.of(new OneNode(), new ZeroNode())));
    }
}

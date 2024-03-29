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
package com.ledmington.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestLogicNot extends TestLogicFunction {

    @BeforeAll
    public static void setup() {
        lf = new LogicNot();
    }

    private static Stream<Arguments> bitStrings() {
        return Stream.of(
                        // all 1-bit strings
                        Stream.of("0", "1"),
                        // all 2-bit strings
                        Stream.of("0", "1").flatMap(s -> Stream.of(s + "0", s + "1")),
                        // all 3-bit strings
                        Stream.of("0", "1")
                                .flatMap(s -> Stream.of(s + "0", s + "1"))
                                .flatMap(s -> Stream.of(s + "0", s + "1")),
                        // all 4-bit strings
                        Stream.of("0", "1")
                                .flatMap(s -> Stream.of(s + "0", s + "1"))
                                .flatMap(s -> Stream.of(s + "0", s + "1"))
                                .flatMap(s -> Stream.of(s + "0", s + "1")),
                        // all 5-bit strings
                        Stream.of("0", "1")
                                .flatMap(s -> Stream.of(s + "0", s + "1"))
                                .flatMap(s -> Stream.of(s + "0", s + "1"))
                                .flatMap(s -> Stream.of(s + "0", s + "1"))
                                .flatMap(s -> Stream.of(s + "0", s + "1")))
                .flatMap(Function.identity())
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("bitStrings")
    public void correctNot(final BitArray in) {
        final BitArray out = new BitArray(in.length());
        for (int i = 0; i < in.length(); i++) {
            out.set(i, !in.get(i));
        }
        assertEquals(out, lf.apply(in));
    }
}

/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington;

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

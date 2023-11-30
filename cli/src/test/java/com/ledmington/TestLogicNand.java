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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Function;
import java.util.stream.Stream;

import com.ledmington.utils.Generators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestLogicNand extends TestLogicFunction {

    @BeforeAll
    public static void setup() {
        lf = new LogicNand();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9})
    public void noNandWithOddNumberOfBits(int n) {
        assertThrows(IllegalArgumentException.class, () -> lf.apply(new BitArray(n)));
    }

    private static Stream<Arguments> bitStrings() {
        return Stream.of(Generators.bitStrings(2), Generators.bitStrings(4), Generators.bitStrings(6))
                .flatMap(Function.identity())
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("bitStrings")
    public void correctNand(final BitArray in) {
        final BitArray out = new BitArray(in.length() / 2);
        for (int i = 0; i < out.length(); i++) {
            out.set(i, !(in.get(i) & in.get(i + out.length())));
        }

        assertEquals(out, lf.apply(in));
    }
}

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Function;
import java.util.stream.Stream;

import com.ledmington.utils.Generators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestBitArray {
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void invalidBits(int n) {
        assertThrows(IllegalArgumentException.class, () -> new BitArray(n));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void correctLength(int n) {
        final BitArray b = new BitArray(n);
        assertEquals(n, b.length());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 10, 11})
    public void invalidGet(int idx) {
        final BitArray b = new BitArray(10);
        assertThrows(IllegalArgumentException.class, () -> b.get(idx));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void initiallyAllFalse(int n) {
        final BitArray b = new BitArray(n);
        for (int i = 0; i < n; i++) {
            assertFalse(b.get(i));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 10, 11})
    public void invalidSet(int idx) {
        final BitArray b = new BitArray(10);
        assertThrows(IllegalArgumentException.class, () -> b.set(idx));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void trueAfterSet(int i) {
        final BitArray b = new BitArray(10);
        b.set(i);
        assertTrue(b.get(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void canSetMultipleTimes(int i) {
        final BitArray b = new BitArray(10);
        b.set(i);
        assertTrue(b.get(i));
        b.set(i);
        assertTrue(b.get(i));
        b.set(i);
        assertTrue(b.get(i));
        b.set(i);
        assertTrue(b.get(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 10, 11})
    public void invalidReset(int idx) {
        final BitArray b = new BitArray(10);
        assertThrows(IllegalArgumentException.class, () -> b.reset(idx));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void falseAfterReset(int i) {
        final BitArray b = new BitArray(10);
        b.set(i);
        b.reset(i);
        assertFalse(b.get(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void canResetMultipleTimes(int i) {
        final BitArray b = new BitArray(10);
        b.set(i);
        b.reset(i);
        assertFalse(b.get(i));
        b.reset(i);
        assertFalse(b.get(i));
        b.reset(i);
        assertFalse(b.get(i));
        b.reset(i);
        assertFalse(b.get(i));
    }

    @Test
    public void nullStringConstructor() {
        assertThrows(NullPointerException.class, () -> new BitArray(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "1a", "a1", "0.0"})
    public void invalidStringConstructor(final String s) {
        assertThrows(IllegalArgumentException.class, () -> new BitArray(s));
    }

    private static Stream<Arguments> bitStrings() {
        return Stream.of(
                        Generators.bitStrings(1),
                        Generators.bitStrings(2),
                        Generators.bitStrings(3),
                        Generators.bitStrings(4),
                        Generators.bitStrings(5))
                .flatMap(Function.identity())
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("bitStrings")
    public void stringConstructor(final String s) {
        final BitArray b = new BitArray(s);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0') {
                assertFalse(b.get(i));
            } else if (s.charAt(i) == '1') {
                assertTrue(b.get(i));
            } else {
                fail(String.format("Invalid input string '%s'", s));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("bitStrings")
    public void stringRepresentation(final String s) {
        final BitArray b = new BitArray(s);
        assertEquals(s, b.toString());
    }
}

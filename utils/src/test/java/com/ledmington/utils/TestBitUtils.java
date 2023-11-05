/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public final class TestBitUtils {
    @ParameterizedTest
    @ValueSource(
            shorts = {
                0x0, 0x1, 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80, 0x100, 0x200, 0x400, 0x800, 0x1000, 0x2000, 0x4000,
                -32_768
            })
    public void hasOneBit(short s) {
        assertTrue(BitUtils.has_one_bit(s), String.format("%,d (0x%04x) has not 1 set bit", s, s));
    }

    private static Stream<Arguments> twoBitShorts() {
        final List<Short> ll = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            for (int j = i + 1; j < 16; j++) {
                ll.add((short) ((1 << i) | (1 << j)));
            }
        }
        return ll.stream().map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("twoBitShorts")
    public void hasNotOneBit(short s) {
        assertFalse(BitUtils.has_one_bit(s), String.format("%,d (0x%04x) has 1 set bit", s, s));
    }

    private static Stream<Arguments> someShorts() {
        return Stream.of(
                Arguments.of((short) 0x0000, 0),
                Arguments.of((short) 1, 1),
                Arguments.of((short) 2, 1),
                Arguments.of((short) 3, 2),
                Arguments.of((short) 0xffff, 16),
                Arguments.of((short) -32_768, 1),
                Arguments.of((short) 0x5555, 8));
    }

    @ParameterizedTest
    @MethodSource("someShorts")
    public void someShorts(final short s, final int nBits) {
        assertEquals(
                nBits,
                BitUtils.popcount(s),
                String.format("%,d (0x%04x) should have %,d set bits but had %,d", s, s, nBits, BitUtils.popcount(s)));
    }
}

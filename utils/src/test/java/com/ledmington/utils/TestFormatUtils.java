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

import java.math.BigInteger;
import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class TestFormatUtils {
    @ParameterizedTest
    @ValueSource(
            longs = {
                -1_234_567_890_123_456_789L,
                -123_456_789_012_345_678L,
                -12_345_678_901_234_567L,
                -1_234_567_890_123_456L,
                -123_456_789_012_345L,
                -12_345_678_901_234L,
                -1_234_567_890_123L,
                -123_456_789_012L,
                -12_345_678_901L,
                -1_234_567_890,
                -123_456_789,
                -12_345_678,
                -1_234_567,
                -123_456,
                -12_345,
                -1_234,
                -123,
                -12,
                -1,
                0,
                1,
                12,
                123,
                1_234,
                12_345,
                123_456,
                1_234_567,
                12_345_678,
                123_456_789,
                1_234_567_890,
                12_345_678_901L,
                123_456_789_012L,
                1_234_567_890_123L,
                12_345_678_901_234L,
                123_456_789_012_345L,
                1_234_567_890_123_456L,
                12_345_678_901_234_567L,
                123_456_789_012_345_678L,
                1_234_567_890_123_456_789L
            })
    void thousands(long n) {
        Locale.setDefault(Locale.US);
        final String expected = String.format("%,d", n);
        final String actual = FormatUtils.thousands(BigInteger.valueOf(n), ",");
        assertEquals(
                expected,
                actual,
                String.format(
                        "Expected number formatted as '%s' but was '%s'. Is the Locale correctly set?",
                        expected, actual));
    }
}

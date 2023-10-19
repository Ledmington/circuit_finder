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
package com.ledmington.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestFormatUtils {
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
    public void thousands(long n) {
        assertEquals(String.format("%,d", n), FormatUtils.thousands(BigInteger.valueOf(n), ","));
    }
}

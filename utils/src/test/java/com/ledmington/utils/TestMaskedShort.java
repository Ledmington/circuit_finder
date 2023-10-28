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

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class TestMaskedShort {

    private static Stream<Arguments> stringOutput() {
        return Stream.of(
                Arguments.of(new MaskedShort((short) 0x0000, (short) 0xffff), 4, "0000"),
                Arguments.of(new MaskedShort((short) 0x0001, (short) 0xffff), 4, "0001"),
                Arguments.of(new MaskedShort((short) 0x0003, (short) 0xffff), 4, "0011"),
                Arguments.of(new MaskedShort((short) 0x0007, (short) 0xffff), 4, "0111"),
                Arguments.of(new MaskedShort((short) 0x000f, (short) 0xffff), 4, "1111"),
                Arguments.of(new MaskedShort((short) 0x001f, (short) 0xffff), 4, "1111"),
                Arguments.of(new MaskedShort((short) 0x000f, (short) 0xfffe), 4, "111-"),
                Arguments.of(new MaskedShort((short) 0x000f, (short) 0xfffd), 4, "11-1"),
                Arguments.of(new MaskedShort((short) 0x000f, (short) 0xfffb), 4, "1-11"),
                Arguments.of(new MaskedShort((short) 0x000f, (short) 0xfff7), 4, "-111"));
    }

    @ParameterizedTest
    @MethodSource("stringOutput")
    public void stringOutput(final MaskedShort ms, final int bits, final String output) {
        assertEquals(output, ms.toString(bits));
    }

    private static Stream<Arguments> equality() {
        return Stream.of(
                Arguments.of(new MaskedShort((short) 0, (short) 0), new MaskedShort((short) 0, (short) 0)),
                Arguments.of(new MaskedShort((short) 1, (short) 0), new MaskedShort((short) 1, (short) 0)),
                Arguments.of(new MaskedShort((short) 2, (short) 0), new MaskedShort((short) 2, (short) 0)),
                Arguments.of(new MaskedShort((short) 3, (short) 0), new MaskedShort((short) 3, (short) 0)),
                Arguments.of(new MaskedShort((short) 0, (short) 0xffff), new MaskedShort((short) 0, (short) 0xffff)),
                Arguments.of(new MaskedShort((short) 1, (short) 0xffff), new MaskedShort((short) 1, (short) 0xffff)),
                Arguments.of(new MaskedShort((short) 2, (short) 0xffff), new MaskedShort((short) 2, (short) 0xffff)),
                Arguments.of(new MaskedShort((short) 3, (short) 0xffff), new MaskedShort((short) 3, (short) 0xffff)),
                Arguments.of(new MaskedShort((short) 0x00ff, (short) 0x00f0), new MaskedShort((short) 0x00f0, (short)
                        0x00f0)),
                Arguments.of(new MaskedShort((short) 0xffff, (short) 0x5555), new MaskedShort((short) 0x5555, (short)
                        0x5555)));
    }

    @ParameterizedTest
    @MethodSource("equality")
    public void equality(final MaskedShort a, final MaskedShort b) {
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}

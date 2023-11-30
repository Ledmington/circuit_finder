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
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

final class TestMaskedShort {

    private static final RandomGenerator rng =
            RandomGeneratorFactory.getDefault().create(System.nanoTime());

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
    void stringOutput(final MaskedShort ms, final int bits, final String output) {
        assertEquals(
                output,
                ms.toString(bits),
                String.format("Expected output formatted as '%s' but was '%s'.", output, ms.toString(bits)));
    }

    private static Stream<Arguments> equality() {
        final List<Short> ls = Stream.concat(
                        Stream.of((short) 0, (short) 1, (short) 2, (short) 3),
                        Stream.generate(() -> (short) rng.nextInt()))
                .distinct()
                .limit(10)
                .toList();
        final List<Arguments> result = new ArrayList<>();
        for (final short a : ls) {
            for (final short b : ls) {
                final short andMask = (short) (a & b);
                final short orMask = (short) (a | b);
                result.add(Arguments.of(new MaskedShort(a, andMask), new MaskedShort(b, andMask)));
                result.add(Arguments.of(new MaskedShort(a, andMask), new MaskedShort(orMask, andMask)));
                result.add(Arguments.of(new MaskedShort(orMask, andMask), new MaskedShort(b, andMask)));
                result.add(Arguments.of(new MaskedShort(orMask, andMask), new MaskedShort(orMask, andMask)));
            }
        }
        return result.stream();
    }

    @ParameterizedTest
    @MethodSource("equality")
    void equality(final MaskedShort a, final MaskedShort b) {
        assertEquals(a, b, String.format("Expected '%s' and '%s' to be equal but .equals() returned false.", a, b));
    }

    @ParameterizedTest
    @MethodSource("equality")
    void equalityHashCode(final MaskedShort a, final MaskedShort b) {
        assertEquals(
                a.hashCode(),
                b.hashCode(),
                String.format(
                        "Expected same hashCode between '%s' and '%s' but they were respectively %,d and %,d.",
                        a, b, a.hashCode(), b.hashCode()));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 16})
    void invalidMaskIndex(int i) {
        assertThrows(IllegalArgumentException.class, () -> new MaskedShort((short) 0, (short) 0).isRelevant(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 16})
    void invalidValueIndex(int i) {
        assertThrows(IllegalArgumentException.class, () -> new MaskedShort((short) 0, (short) 0).isSet(i));
    }

    private static Stream<Arguments> randomMaskedShorts() {
        return Stream.generate(() -> new MaskedShort((short) rng.nextInt(), (short) rng.nextInt()))
                .distinct()
                .limit(10)
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("randomMaskedShorts")
    void bitAPI(final MaskedShort ms) {
        for (int i = 0; i < 16; i++) {
            final boolean expected = (ms.mask() & (1 << i)) != 0;
            final boolean actual = ms.isRelevant(i);
            assertEquals(
                    expected, actual, String.format("Expected %s for mask bit %,d but was %s", expected, i, actual));
        }

        for (int i = 0; i < 16; i++) {
            final boolean expected = ((ms.mask() & ms.value()) & (1 << i)) != 0;
            final boolean actual = ms.isSet(i);
            assertEquals(
                    expected, actual, String.format("Expected %s for value bit %,d but was %s", expected, i, actual));
        }
    }
}

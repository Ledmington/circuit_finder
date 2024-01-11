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
package com.ledmington.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public final class TestShortList {

    private ShortList sl;

    @BeforeEach
    void setup() {
        sl = new ShortList();
    }

    @Test
    void initiallyEmpty() {
        assertTrue(sl.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0})
    void invalidInitialSize(int n) {
        assertThrows(IllegalArgumentException.class, () -> new ShortList(n));
    }

    @Test
    void sizeGrows() {
        for (int i = 0; i < 100; i++) {
            assertEquals(i, sl.size());
            sl.add((short) i);
        }
    }

    @Test
    void correctContains() {
        for (int i = 0; i < 100; i++) {
            sl.add((short) i);
            assertTrue(sl.contains((short) i));
        }
    }

    @Test
    void incorrectContains() {
        for (int i = 0; i < 100; i++) {
            sl.add((short) i);
            assertFalse(sl.contains((short) -1));
            assertFalse(sl.contains((short) (i + 1)));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void checkString(int n) {
        final RandomGenerator rng = RandomGeneratorFactory.getDefault().create(System.nanoTime());
        final List<Short> sl = new ShortList();
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < n; i++) {
            final short s = (short) rng.nextInt();
            sl.add(s);
            sb.append(s);
            if (i != n - 1) {
                sb.append(',');
            }
        }
        sb.append(']');
        assertEquals(sb.toString(), sl.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void checkEquality(int n) {
        final RandomGenerator rng = RandomGeneratorFactory.getDefault().create(System.nanoTime());
        final List<Short> sl1 = new ShortList();
        final List<Short> sl2 = new ShortList();
        for (int i = 0; i < n; i++) {
            final short s = (short) rng.nextInt();
            sl1.add(s);
            sl2.add(s);
        }
        assertEquals(sl1, sl2);
    }

    private static Stream<Arguments> randomLists() {
        final RandomGenerator rng = RandomGeneratorFactory.getDefault().create(System.nanoTime());
        return Stream.generate(() -> new ShortList(Stream.generate(() -> (short) rng.nextInt())
                        .limit(rng.nextInt(2, 10))
                        .toList()))
                .limit(10)
                .flatMap(l -> {
                    final List<Short> shuffled = new ShortList(l);
                    {
                        final short s = shuffled.get(0);
                        shuffled.set(0, shuffled.get(1));
                        shuffled.set(1, s);
                    }
                    final List<Short> shorter = new ShortList(l);
                    shorter.remove(shorter.get(0));
                    final List<Short> longer = new ShortList(l);
                    longer.add((short) rng.nextInt());
                    return Stream.of(Arguments.of(l, shuffled), Arguments.of(l, shorter), Arguments.of(l, longer));
                });
    }

    @ParameterizedTest
    @MethodSource("randomLists")
    void checkDiversity(final List<Short> a, final List<Short> b) {
        assertNotEquals(a, b);
    }
}

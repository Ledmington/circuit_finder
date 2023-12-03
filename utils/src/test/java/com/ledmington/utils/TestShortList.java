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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
}

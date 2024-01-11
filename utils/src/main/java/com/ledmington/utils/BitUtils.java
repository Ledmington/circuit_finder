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

public final class BitUtils {

    private BitUtils() {}

    /**
     * Returns true whether the given input has only one 1.
     * Note: returns true also when x == 0.
     */
    public static boolean has_one_bit(final short x) {
        short y = (short) (x - 1);
        return (x & y) == 0;
    }

    /**
     * Population count for shorts (Integer.bitCount doesn't work
     * properly with non-ints).
     */
    public static int popcount(final short in) {
        int x = in & 0xffff;
        x = (x & 0x5555) + ((x >> 1) & 0x5555);
        x = (x & 0x3333) + ((x >> 2) & 0x3333);
        x = (x & 0x0f0f) + ((x >> 4) & 0x0f0f);
        x = (x & 0x00ff) + ((x >> 8) & 0x00ff);
        return x;
    }
}

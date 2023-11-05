/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

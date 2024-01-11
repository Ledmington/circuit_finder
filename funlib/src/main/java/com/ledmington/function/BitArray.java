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
package com.ledmington.function;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public final class BitArray {

    /*
     * This is the bits array stored in little-endian (or MSB-0).
     * This means that v[0] is the MSB and
     * v[v.length-1] is the LSB.
     */
    private final boolean[] v;

    /*
     * Little cache utilities to improve performance.
     */
    private boolean isCachedHashCodeSet = false;
    private int cachedHashCode = -1;

    /**
     * Creates a new immutable BitArray with space for the given number of bits.
     */
    public BitArray(int bits) {
        if (bits < 1) {
            throw new IllegalArgumentException(
                    String.format("Invalid 'bits' value: should have been >=1 but was %,d", bits));
        }
        this.v = new boolean[bits];
    }

    public BitArray(final String bits) {
        Objects.requireNonNull(bits);
        if (bits.isEmpty()) {
            throw new IllegalArgumentException("An empty String is not allowed");
        }
        this.v = new boolean[bits.length()];
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '0') {
                this.v[i] = false;
            } else if (bits.charAt(i) == '1') {
                this.v[i] = true;
            } else {
                throw new IllegalArgumentException(
                        String.format("Invalid character found at index %,d: was '%c'", i, bits.charAt(i)));
            }
        }
    }

    public static BitArray convert(int bits, final BigInteger x) {
        final BitArray a = new BitArray(bits);
        for (int i = bits - 1; i >= 0; i--) {
            a.v[i] = x.testBit(i);
        }
        return a;
    }

    public static BitArray concat(final BitArray a, final BitArray b) {
        final BitArray c = new BitArray(a.length() + b.length());

        for (int i = 0; i < a.length(); i++) {
            c.v[i] = a.get(i);
        }
        for (int i = 0; i < b.length(); i++) {
            c.v[i + a.length()] = b.get(i);
        }
        return c;
    }

    public int length() {
        return v.length;
    }

    private void assertIndexIsValid(int i) {
        if (i < 0 || i >= v.length) {
            throw new IllegalArgumentException(String.format(
                    "Invalid 'index' value: should have been between 0 and %,d but was %,d", v.length, i));
        }
    }

    public boolean get(int i) {
        assertIndexIsValid(i);
        return v[i];
    }

    /**
     * Sets the i-th bit to the given value.
     *
     * @param i
     *      The index of the bit to be set.
     */
    public void set(int i, boolean value) {
        assertIndexIsValid(i);
        isCachedHashCodeSet = (v[i] == value);
        v[i] = value;
    }

    /**
     * Sets the i-th bit to 1.
     *
     * @param i
     *      The index of the bit to be set to 1.
     */
    public void set(int i) {
        assertIndexIsValid(i);
        isCachedHashCodeSet = v[i];
        v[i] = true;
    }

    /**
     * Sets the i-th bit to 0.
     *
     * @param i
     *      The index of the bits to be set to 0.
     */
    public void reset(int i) {
        assertIndexIsValid(i);
        isCachedHashCodeSet = !v[i];
        v[i] = false;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (boolean b : v) {
            sb.append(b ? "1" : "0");
        }
        return sb.toString();
    }

    public int hashCode() {
        if (isCachedHashCodeSet) {
            return cachedHashCode;
        }

        int h = 17;
        for (boolean b : v) {
            h = 31 * h + (b ? 1 : 0);
        }

        cachedHashCode = h;
        isCachedHashCodeSet = true;
        return h;
    }

    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        final BitArray b = (BitArray) other;
        return Arrays.equals(this.v, b.v);
    }
}

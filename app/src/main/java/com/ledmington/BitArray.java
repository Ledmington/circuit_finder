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
package com.ledmington;

import java.math.BigInteger;
import java.util.Arrays;

public final class BitArray {

    private final boolean[] v;
    private boolean isCachedHashCodeSet = false;
    private int cachedHashCode = -1;

    public BitArray(int bits) {
        this.v = new boolean[bits];
    }

    public static BitArray convert(int bits, final BigInteger x) {
        final BitArray a = new BitArray(bits);
        for (int i = bits - 1; i >= 0; i--) {
            a.v[i] = x.testBit(i);
        }
        return a;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = v.length - 1; i >= 0; i--) {
            sb.append(v[i] ? "1" : "0");
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

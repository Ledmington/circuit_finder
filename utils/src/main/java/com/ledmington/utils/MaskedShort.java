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

public final class MaskedShort {

    private final short v;
    private final short m;
    private boolean isCachedHashCodeSet = false;
    private int cachedHashCode = -1;

    public MaskedShort(final short v, final short m) {
        this.v = (short) (v & m);
        this.m = m;
    }

    public short value() {
        return v;
    }

    public short mask() {
        return m;
    }

    private void assertValidIndex(int i) {
        if (i < 0 || i >= 16) {
            throw new IllegalArgumentException(String.format("Invalid bit index %,d for a short", i));
        }
    }

    /**
     * Returns true is the i-th bit of the value is set.
     * 0-indexed.
     */
    public boolean isSet(int i) {
        assertValidIndex(i);
        return (v & (1 << i)) != 0;
    }

    /**
     * Returns true if the i-th bit of the value is relevant.
     * 0-indexed.
     */
    public boolean isRelevant(int i) {
        assertValidIndex(i);
        return (m & (1 << i)) != 0;
    }

    @Override
    public String toString() {
        return toString(16);
    }

    public String toString(final int nBits) {
        if (nBits <= 0 || nBits > 16) {
            throw new IllegalArgumentException(String.format(
                    "Invalid number of bits to represent a short: expected between 1 and 16 but was %,d", nBits));
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < nBits; i++) {
            final short tmp = (short) (1 << (nBits - 1 - i));
            if ((m & tmp) == 0) {
                s.append('-');
            } else {
                s.append((v & tmp) != 0 ? '1' : '0');
            }
        }
        return s.toString();
    }

    @Override
    public int hashCode() {
        if (isCachedHashCodeSet) {
            return cachedHashCode;
        }
        cachedHashCode = 17 + 31 * this.v + 31 * 31 * this.m;
        isCachedHashCodeSet = true;
        return cachedHashCode;
    }

    @Override
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
        final MaskedShort ms = (MaskedShort) other;
        return ((this.m & this.v) == (ms.m & ms.v));
    }
}

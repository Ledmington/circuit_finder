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

public final class MaskedShort {

    private final short value;
    private final short mask;

    public MaskedShort(final short v, final short m) {
        this.value = v;
        this.mask = m;
    }

    public short value() {
        return value;
    }

    public short mask() {
        return mask;
    }

    /**
     * Returns true is the i-th bit of the value is set.
     * 0-indexed.
     */
    public boolean isSet(int i) {
        return (value & (1 << i)) != 0;
    }

    /**
     * Returns true if the i-th bit of the value is relevant.
     * 0-indexed.
     */
    public boolean isRelevant(int i) {
        return (mask & (1 << i)) != 0;
    }

    public String toString() {
        return toString(16);
    }

    public String toString(final int nBits) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < nBits; i++) {
            final short tmp = (short) (1 << (nBits - 1 - i));
            if ((mask & tmp) == 0) {
                s.append('-');
            } else {
                s.append((value & tmp) != 0 ? '1' : '0');
            }
        }
        return s.toString();
    }

    public int hashCode() {
        return 17 + 31 * (this.value & this.mask);
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
        final MaskedShort ms = (MaskedShort) other;
        return (value & mask) == (ms.value & ms.mask);
    }
}

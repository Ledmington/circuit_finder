/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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
        // FIXME: avoid having to rely on toString
        return this.toString().equals(ms.toString());
    }
}

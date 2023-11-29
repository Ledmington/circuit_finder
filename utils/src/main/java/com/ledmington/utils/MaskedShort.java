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

/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington;

public final class SignedSum extends AbstractLogicFunction {

    public int inputBits(int n) {
        assertValidBits(n);
        return 2 * n;
    }

    public int outputBits(int n) {
        assertValidBits(n);
        return n;
    }

    public BitArray apply(final BitArray bits) {
        final int length = bits.length();

        if (length % 2 != 0 || length < 4) {
            throw new IllegalArgumentException(
                    String.format("Invalid number of input bits: expected an even number >=4 but was %,d", length));
        }

        // we assume that 'bits' contains two same-sized arrays
        // representing the signed integers 'a' and 'b'
        final int halfLength = length / 2;
        final boolean[] a = new boolean[halfLength];
        final boolean[] b = new boolean[halfLength];
        for (int i = 0; i < halfLength; i++) {
            a[i] = bits.get(i);
            b[i] = bits.get(halfLength + i);
        }

        final BitArray c = new BitArray(halfLength);
        boolean carry = false;
        for (int i = halfLength - 1; i >= 0; i--) {
            c.set(i, a[i] ^ b[i] ^ carry);
            carry = (a[i] & b[i]) | (a[i] & carry) | (b[i] & carry);
        }

        return c;
    }
}

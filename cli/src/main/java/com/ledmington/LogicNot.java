/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington;

public final class LogicNot extends AbstractLogicFunction {
    @Override
    public int inputBits(int n) {
        assertValidBits(n);
        return n;
    }

    @Override
    public int outputBits(int n) {
        assertValidBits(n);
        return n;
    }

    @Override
    public BitArray apply(final BitArray in) {
        final BitArray out = new BitArray(in.length());

        for (int i = 0; i < in.length(); i++) {
            out.set(i, !in.get(i));
        }

        return out;
    }
}

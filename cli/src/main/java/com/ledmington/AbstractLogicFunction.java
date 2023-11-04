/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington;

public abstract class AbstractLogicFunction implements LogicFunction {
    protected void assertValidBits(int n) {
        if (n < 1) {
            throw new IllegalArgumentException(
                    String.format("Invalid number of bits: should have been >=1 but was %,d", n));
        }
    }
}

/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public abstract class TestLogicFunction {

    protected static LogicFunction lf;

    @Test
    public void noNullInput() {
        assertThrows(NullPointerException.class, () -> lf.apply(null));
    }

    @Test
    public void noEmptyInputBits() {
        assertThrows(IllegalArgumentException.class, () -> lf.apply(new BitArray(0)));
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0})
    public void invalidInputBits(int n) {
        assertThrows(IllegalArgumentException.class, () -> lf.inputBits(n));
    }

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, 0})
    public void invalidOutputBits(int n) {
        assertThrows(IllegalArgumentException.class, () -> lf.outputBits(n));
    }
}

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

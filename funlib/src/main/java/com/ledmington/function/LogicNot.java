/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2024 Filippo Barbari <filippo.barbari@gmail.com>
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
package com.ledmington.function;

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

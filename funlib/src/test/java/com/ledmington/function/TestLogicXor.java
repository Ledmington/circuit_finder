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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.Function;
import java.util.stream.Stream;

import com.ledmington.utils.Generators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestLogicXor extends TestLogicFunction {

    @BeforeAll
    public static void setup() {
        lf = new LogicXor();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 7, 9})
    public void noOrWithOddNumberOfBits(int n) {
        assertThrows(IllegalArgumentException.class, () -> lf.apply(new BitArray(n)));
    }

    private static Stream<Arguments> bitStrings() {
        return Stream.of(Generators.bitStrings(2), Generators.bitStrings(4), Generators.bitStrings(6))
                .flatMap(Function.identity())
                .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("bitStrings")
    public void correctOr(final BitArray in) {
        final BitArray out = new BitArray(in.length() / 2);
        for (int i = 0; i < out.length(); i++) {
            out.set(i, in.get(i) ^ in.get(i + out.length()));
        }

        assertEquals(out, lf.apply(in));
    }
}

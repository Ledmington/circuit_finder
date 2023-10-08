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
package com.ledmington;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestSignedSum {

    private final SignedSum ss = new SignedSum();

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 7, 9})
    public void noSumWithOddNumberOfBits(int n) {
        assertThrows(IllegalArgumentException.class, () -> ss.apply(new BitArray(n)));
    }

    private static Stream<Arguments> signedAdditions() {
        final List<Arguments> args = new ArrayList<>();
        final Map<Integer, BitArray> signedIntegers = new TreeMap<>();
        signedIntegers.put(0, new BitArray("0000"));
        signedIntegers.put(1, new BitArray("0001"));
        signedIntegers.put(2, new BitArray("0010"));
        signedIntegers.put(3, new BitArray("0011"));
        signedIntegers.put(4, new BitArray("0100"));
        signedIntegers.put(5, new BitArray("0101"));
        signedIntegers.put(6, new BitArray("0110"));
        signedIntegers.put(7, new BitArray("0111"));
        signedIntegers.put(-8, new BitArray("1000"));
        signedIntegers.put(-7, new BitArray("1001"));
        signedIntegers.put(-6, new BitArray("1010"));
        signedIntegers.put(-5, new BitArray("1011"));
        signedIntegers.put(-4, new BitArray("1100"));
        signedIntegers.put(-3, new BitArray("1101"));
        signedIntegers.put(-2, new BitArray("1110"));
        signedIntegers.put(-1, new BitArray("1111"));

        for (final Integer a : signedIntegers.keySet()) {
            for (final Integer b : signedIntegers.keySet()) {
                int result = a + b;
                if (result > 7) {
                    result -= 16;
                }
                if (result < -8) {
                    result += 16;
                }
                if (!signedIntegers.containsKey(result)) {
                    fail(String.format("No BitArray available for %,d", result));
                }
                args.add(Arguments.of(signedIntegers.get(a), signedIntegers.get(b), signedIntegers.get(result)));
            }
        }

        return args.stream();
    }

    @ParameterizedTest
    @MethodSource("signedAdditions")
    public void correctSignedSum(final BitArray a, final BitArray b, final BitArray out) {
        final BitArray actual = ss.apply(BitArray.concat(a, b));
        assertEquals(
                out,
                actual,
                String.format("Wrong signed sum result: %s + %s should have been %s but was %s", a, b, out, actual));
    }

    @ParameterizedTest
    @MethodSource("signedAdditions")
    public void commutativity(final BitArray a, final BitArray b, final BitArray out) {
        assertEquals(ss.apply(BitArray.concat(a, b)), ss.apply(BitArray.concat(b, a)));
    }
}

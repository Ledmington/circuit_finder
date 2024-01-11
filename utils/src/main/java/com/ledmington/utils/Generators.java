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
package com.ledmington.utils;

import java.util.stream.Stream;

public final class Generators {
    private Generators() {}

    public static Stream<String> bitStrings(int length) {
        if (length < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid max length of bit strings: should have been >=0 but was %,d", length));
        }

        if (length == 0) {
            return Stream.empty();
        }

        Stream<String> s = Stream.of("0", "1");
        for (int i = 0; i < length - 1; i++) {
            s = s.flatMap(x -> Stream.of(x + "0", x + "1"));
        }
        return s;
    }
}

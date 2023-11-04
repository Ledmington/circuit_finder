/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

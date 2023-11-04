/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington;

import java.util.function.Function;

public interface LogicFunction extends Function<BitArray, BitArray> {
    int inputBits(int n);

    int outputBits(int n);
}

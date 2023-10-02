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

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public final class Main {

    public static void main(final String[] args) {
        final int bits = 32;
        final RandomGenerator rng = RandomGeneratorFactory.getDefault().create(System.nanoTime());
        Circuit best = new Circuit(bits, rng);
        Circuit current = new Circuit(best);

        for (int i = 0; i < 123456789; i++) {
            current.mutate();
            final int input = rng.nextInt(0, Integer.MAX_VALUE);
            if (current.evaluate(input) == (int) Math.sqrt(input)) {
                best = new Circuit(current);
                System.out.println(best);
            }

            if (i % 1_000_000 == 0) {
                System.out.printf("%,d\n", i);
            }
        }
    }
}

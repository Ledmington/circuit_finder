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

import java.util.Objects;
import java.util.random.RandomGenerator;

public final class Circuit {
    private final int nBits;
    private final Switch[][] andPlane;
    private final Switch[][] orPlane;
    private final RandomGenerator rng;

    public Circuit(final int nBits, final RandomGenerator rng) {
        Objects.requireNonNull(rng);
        this.rng = rng;
        this.nBits = nBits;
        this.andPlane = new Switch[nBits][nBits];
        this.orPlane = new Switch[nBits][nBits];
        for (int i = 0; i < nBits; i++) {
            for (int j = 0; j < nBits; j++) {
                andPlane[i][j] = Switch.random(rng);
                orPlane[i][j] = Switch.random(rng);
            }
        }
    }

    public Circuit(final Circuit other) {
        Objects.requireNonNull(other);
        this.rng = other.rng;
        this.nBits = other.nBits;
        this.andPlane = new Switch[nBits][nBits];
        this.orPlane = new Switch[nBits][nBits];
        for (int i = 0; i < nBits; i++) {
            for (int j = 0; j < nBits; j++) {
                andPlane[i][j] = other.andPlane[i][j];
                orPlane[i][j] = other.orPlane[i][j];
            }
        }
    }

    public void mutate() {
        if (rng.nextBoolean()) {
            // mutate AND plane
            final int r = rng.nextInt(0, nBits);
            final int c = rng.nextInt(0, nBits);
            Switch s;
            do {
                s = Switch.random(rng);
            } while (s == andPlane[r][c]);
            andPlane[r][c] = s;
        } else {
            // mutate OR plane
            final int r = rng.nextInt(0, nBits);
            final int c = rng.nextInt(0, nBits);
            Switch s;
            do {
                s = Switch.random(rng);
            } while (s == orPlane[r][c]);
            orPlane[r][c] = s;
        }
    }

    private static void debug(final boolean[] b) {
        for (boolean value : b) {
            System.out.printf("%s", value ? "1" : "0");
        }
        System.out.println();
    }

    public int evaluate(final int in) {
        final boolean[] in_bits = new boolean[nBits];
        for (int i = 0; i < nBits; i++) {
            in_bits[i] = (in & (1 << i)) != 0x00000000;
        }

        // debug(in_bits);
        // System.out.println();

        // evaluate AND plane
        final boolean[] and_plane_out = new boolean[nBits];
        System.arraycopy(in_bits, 0, and_plane_out, 0, nBits);
        for (int i = 0; i < nBits; i++) {
            for (int j = 0; j < nBits; j++) {
                switch (andPlane[i][j]) {
                    case A:
                        and_plane_out[j] &= in_bits[i];
                        break;
                    case NOT_A:
                        and_plane_out[j] &= !(in_bits[i]);
                        break;
                    case UNUSED:
                        // do nothing
                        break;
                    default:
                        throw new IllegalStateException(
                                String.format("Weird Switch value found; \"%s\"", andPlane[i][j].toString()));
                }
            }
            // debug(and_plane_out);
        }
        // System.out.println();

        // evaluate OR plane
        final boolean[] or_plane_out = new boolean[nBits];
        System.arraycopy(and_plane_out, 0, or_plane_out, 0, nBits);
        for (int i = 0; i < nBits; i++) {
            for (int j = 0; j < nBits; j++) {
                switch (orPlane[i][j]) {
                    case A:
                        or_plane_out[j] |= and_plane_out[i];
                        break;
                    case NOT_A:
                        or_plane_out[j] |= !(and_plane_out[i]);
                        break;
                    case UNUSED:
                        // do nothing
                        break;
                    default:
                        throw new IllegalStateException(
                                String.format("Weird Switch value found; \"%s\"", andPlane[i][j].toString()));
                }
            }
            // debug(or_plane_out);
        }
        // System.out.println();

        // recompose output
        int out = 0x00000000;
        for (int i = 0; i < nBits; i++) {
            if (or_plane_out[i]) {
                out |= 1 << i;
            } else {
                out &= ~(1 << i);
            }
        }

        return out;
    }
}

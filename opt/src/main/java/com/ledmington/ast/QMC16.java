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
package com.ledmington.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.ledmington.utils.MiniLogger;

/**
 * Implementation of Quine-McCluskey algorithm (optimized for 16 bits).
 * <a href="https://www.tandfonline.com/doi/abs/10.1080/00029890.1952.11988183">Original paper</a>.
 */
public final class QMC16 {

    private static final MiniLogger logger = MiniLogger.getLogger("qmc");

    private QMC16() {}

    public record MaskedShort(short value, short mask) {
        /**
         * Returns true is the i-th bit of the value is set.
         * 0-indexed.
         */
        public boolean isSet(int i) {
            return (value & (1 << i)) != 0;
        }

        /**
         * Returns true if the i-th bit of the value is relevant.
         * 0-indexed.
         */
        public boolean isRelevant(int i) {
            return (mask & (1 << i)) != 0;
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                final short tmp = (short) (1 << (15 - i));
                if ((mask & tmp) == 0) {
                    s.append("-");
                } else {
                    s.append((value & tmp) != 0 ? "1" : "0");
                }
            }
            return s.toString();
        }
    }

    private static int popcount(short in) {
        int x = in & 0xffff;
        x = (x & 0x5555) + ((x >> 1) & 0x5555);
        x = (x & 0x3333) + ((x >> 2) & 0x3333);
        x = (x & 0x0f0f) + ((x >> 4) & 0x0f0f);
        x = (x & 0x00ff) + ((x >> 8) & 0x00ff);
        return x;
    }

    public static List<MaskedShort> minimize(final int nBits, final List<Short> ones) {
        if (nBits < 1 || nBits > 16) {
            throw new IllegalArgumentException(
                    String.format("Illegal number of bits: should have been between 1 and 16 but was %,d", nBits));
        }

        // placing a 0 where the bits are not relevant
        final short mask = (short) (0xffff >> (16 - nBits));
        logger.debug("nBits: %,d -> mask: 0x%04x", nBits, mask);
        if (popcount(mask) != nBits) {
            throw new RuntimeException(
                    String.format("Wrong mask created: should have had %,d 1s but had %,d", nBits, popcount(mask)));
        }

        List<MaskedShort> base =
                new ArrayList<>(ones.stream().map(s -> new MaskedShort(s, mask)).toList());
        List<MaskedShort> next;
        List<MaskedShort> result = new ArrayList<>();

        for (int it = 0; it < nBits; it++) {
            logger.debug("Computing size-%,d prime implicants", 1 << it);

            final int length = base.size();
            // final int maxNextLength = (length * (length - 1)) / 2;

            // next = new ArrayList<>(maxNextLength);
            next = new ArrayList<>();

            logger.debug("Initial size: %,d", base.size());
            System.out.print("base: ");
            for (final MaskedShort ms : base) {
                System.out.printf("%s, ", ms.toString());
            }
            System.out.println();

            // TODO: divide inputs based on number of 1s and check only successive groups
            final boolean[] used = new boolean[length];
            for (int i = 0; i < length; i++) {
                final short first = base.get(i).value();
                final short firstMask = base.get(i).mask();
                for (int j = i + 1; j < length; j++) {
                    final short second = base.get(j).value();
                    final short secondMask = base.get(j).mask();
                    if (firstMask != secondMask) {
                        continue;
                    }

                    final short diff = (short) ((first & firstMask) ^ (second & secondMask));
                    if (Integer.bitCount(diff) == 1 && !next.contains(base.get(i))) {
                        // only one bit of difference
                        next.add(new MaskedShort(first, (short) (firstMask & (~diff))));
                        used[i] = true;
                        used[j] = true;
                    }
                }

                if (!used[i]) {
                    // this implicant was not used to compute the "next size" implicants
                    result.add(base.get(i));
                }
            }

            for (final MaskedShort ms : next) {
                System.out.printf("%s, ", ms.toString());
            }
            System.out.println();

            logger.debug("Final size: %,d", next.size());
            logger.debug("Result size: %,d", result.size());

            base = next;
        }

        result = new ArrayList<>(new HashSet<>(result));

        System.out.print("result: ");
        for (final MaskedShort ms : result) {
            System.out.printf("%s, ", ms.toString());
        }
        System.out.println();

        return result;
    }
}

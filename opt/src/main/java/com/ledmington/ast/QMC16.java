/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.ledmington.utils.MaskedShort;
import com.ledmington.utils.MiniLogger;

/**
 * Implementation of Quine-McCluskey algorithm (optimized for 16 bits).
 * <a href="https://www.tandfonline.com/doi/abs/10.1080/00029890.1952.11988183">Original paper</a>.
 */
public final class QMC16 {

    private static final MiniLogger logger = MiniLogger.getLogger("qmc16");

    private QMC16() {}

    private static int popcount(final short in) {
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
        List<MaskedShort> next = new ArrayList<>();
        List<MaskedShort> result = new ArrayList<>();

        for (int it = 0; it < nBits; it++) {
            logger.debug("Computing size-%,d prime implicants", 1 << it);

            final int length = base.size();

            logger.debug("Initial size: %,d", base.size());

            // TODO: divide inputs based on number of 1s and check only successive groups
            //  (helps only in the first iterations)
            // TODO 2: divide inputs in groups with the same mask to greatly reduce checks
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
                    // only one bit of difference
                    if (Integer.bitCount(diff) == 1) {
                        final short newMask = (short) (firstMask & (~diff));
                        final MaskedShort toBeAdded = new MaskedShort((short) (first & newMask), newMask);
                        if (!next.contains(toBeAdded)) {
                            next.add(toBeAdded);
                        }
                        used[i] = true;
                        used[j] = true;
                    }
                }
            }

            for (int i = 0; i < length; i++) {
                if (!used[i]) {
                    // this implicant was not used to compute the "next size" implicants
                    // apply the mask before adding
                    result.add(new MaskedShort(
                            (short) (base.get(i).value() & base.get(i).mask()),
                            base.get(i).mask()));
                }
            }

            logger.debug("next size: %,d", next.size());
            logger.debug("Result size: %,d", result.size());

            base = new ArrayList<>(new HashSet<>(next));
            next = new ArrayList<>();
        }

        result = new ArrayList<>(new HashSet<>(result));

        logger.debug("Result size: %,d", result.size());

        // Building prime implicant chart
        // a big boolean table: one row for each final minterm and one column for each of the starting input 1s
        final boolean[][] chart = new boolean[result.size()][ones.size()];
        logger.debug(
                "The prime implicant chart is %,dx%,d: %,d bytes",
                result.size(), ones.size(), result.size() * ones.size());
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < ones.size(); j++) {
                chart[i][j] =
                        // checking that the 1s are in the right place
                        (result.get(i).value() & result.get(i).mask() & ones.get(j))
                                        == result.get(i).value()
                                &&
                                // checking that the 0s are in the right place
                                (~(~result.get(i).value() & result.get(i).mask() & ~ones.get(j))
                                                & result.get(i).mask())
                                        == result.get(i).value();
            }
        }

        // useful for debugging
        // printChart(chart, result.size(), ones.size());

        final List<MaskedShort> finalResult = new ArrayList<>();

        // Choosing the essential prime implicants: rows of the chart with only one true value
        int epiIdx = findEssentialPrimeImplicant(chart, result.size(), ones.size());
        while (epiIdx != -1) {
            finalResult.add(result.get(epiIdx));

            // zero-ing the selected row and all the columns where the selected row is true
            for (int c = 0; c < ones.size(); c++) {
                if (chart[epiIdx][c]) {
                    for (int r = 0; r < result.size(); r++) {
                        chart[r][c] = false;
                    }
                }
                chart[epiIdx][c] = false;
            }

            // printChart(chart, result.size(), ones.size());

            epiIdx = findEssentialPrimeImplicant(chart, result.size(), ones.size());
        }

        return finalResult;
    }

    private static void printChart(final boolean[][] chart, final int rows, final int columns) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sb.append(chart[i][j] ? '1' : '0');
            }
            if (i != rows - 1) {
                sb.append('\n');
            }
        }
        logger.debug(sb.toString());
    }

    private static int findEssentialPrimeImplicant(final boolean[][] chart, final int rows, final int columns) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!chart[r][c]) {
                    continue;
                }

                int count = 0;
                for (int i = 0; i < rows; i++) {
                    count += chart[i][c] ? 1 : 0;
                }

                if (count == 1) {
                    return r;
                }
            }
        }
        return -1;
    }
}

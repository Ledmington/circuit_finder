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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.ledmington.utils.BitUtils;
import com.ledmington.utils.MaskedShort;
import com.ledmington.utils.MiniLogger;

/**
 * Implementation of Quine-McCluskey algorithm (optimized for 16 bits).
 * <a href="https://www.tandfonline.com/doi/abs/10.1080/00029890.1952.11988183">Original paper</a>.
 */
public final class QMC16 {

    private static final MiniLogger logger = MiniLogger.getLogger("qmc16");

    public QMC16() {}

    public List<MaskedShort> minimize(final int nBits, final List<Short> ones) {
        if (nBits < 1 || nBits > 16) {
            throw new IllegalArgumentException(
                    String.format("Illegal number of bits: should have been between 1 and 16 but was %,d", nBits));
        }

        // placing a 0 where the bits are not relevant
        final short mask = (short) (0xffff >> (16 - nBits));
        logger.debug("nBits: %,d -> initial mask: 0x%04x", nBits, mask);
        if (BitUtils.popcount(mask) != nBits) {
            throw new RuntimeException(String.format(
                    "Wrong mask created: should have had %,d 1s but had %,d", nBits, BitUtils.popcount(mask)));
        }

        Map<Short, List<Short>> base = new HashMap<>();
        base.put(mask, new ArrayList<>());
        for (final short s : ones) {
            base.get(mask).add((short) (s & mask));
        }
        Map<Short, List<Short>> next = new HashMap<>();
        List<MaskedShort> result = new ArrayList<>();

        for (int it = 0; it < nBits; it++) {
            logger.debug("Computing size-%,d prime implicants", 1 << it);

            logger.debug(
                    "Initial size: %,d divided into %,d groups",
                    base.values().stream().mapToInt(List::size).sum(), base.size());

            for (final short m : base.keySet()) {
                final List<Short> elementsWithSameMask = base.get(m);
                final int length = elementsWithSameMask.size();
                final boolean[] used = new boolean[length];

                for (int i = 0; i < length; i++) {
                    final short first = elementsWithSameMask.get(i);
                    for (int j = i + 1; j < length; j++) {
                        final short second = elementsWithSameMask.get(j);

                        final short diff = (short) (first ^ second);

                        if (BitUtils.has_one_bit(diff)) {
                            final short newMask = (short) (m & (~diff));
                            final short newValue = (short) (first & newMask);

                            if (!next.containsKey(newMask)) {
                                next.put(newMask, new ArrayList<>());
                            }
                            if (!next.get(newMask).contains(newValue)) {
                                next.get(newMask).add(newValue);
                            }

                            used[i] = true;
                            used[j] = true;
                        }
                    }
                }

                for (int i = 0; i < length; i++) {
                    if (!used[i]) {
                        // This implicant was not used to compute the "next size" implicants.
                        // Apply the mask before adding.
                        final MaskedShort toBeAdded =
                                new MaskedShort((short) (base.get(m).get(i) & m), m);
                        result.add(toBeAdded);
                        logger.debug(
                                "The value 0x%04x with mask 0x%04x was not used, adding %s to the result",
                                base.get(m).get(i), m, toBeAdded);
                    }
                }
            }

            logger.debug(
                    "next size: %,d",
                    next.values().stream().mapToInt(List::size).sum());
            logger.debug("Result size: %,d", result.size());

            base = new HashMap<>();
            for (final short m : next.keySet()) {
                // TODO: check if the HashSet is useful
                base.put(m, new ArrayList<>(new HashSet<>(next.get(m))));
            }
            next = new HashMap<>();
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

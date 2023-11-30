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
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.qmc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.ledmington.utils.BitUtils;
import com.ledmington.utils.MaskedShort;
import com.ledmington.utils.MiniLogger;

/**
 * Implementation of Quine-McCluskey algorithm (optimized for 16 bits).
 * <a href="https://www.tandfonline.com/doi/abs/10.1080/00029890.1952.11988183">Original paper</a>.
 */
public final class QMC16_V2 implements QMC16 {

    private static final MiniLogger logger = MiniLogger.getLogger("qmc16");
    private final ExecutorService executor;

    public QMC16_V2(int nThreads) {
        final ThreadFactory customTF = new ThreadFactory() {
            private int n = 0;

            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(r, String.format("qmc16-th%d", n++));
            }
        };

        this.executor = Executors.newFixedThreadPool(nThreads, customTF);

        Runtime.getRuntime()
                .addShutdownHook(new Thread(
                        () -> {
                            // executors uglyness
                            if (!executor.isShutdown()) {
                                executor.shutdown();
                                while (true) {
                                    try {
                                        if (executor.awaitTermination(1, TimeUnit.SECONDS)) break;
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        },
                        "executor-killer"));
    }

    @Override
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

        Map<Short, List<Short>> base = new ConcurrentHashMap<>();
        base.put(mask, Collections.synchronizedList(new ArrayList<>()));
        for (final short s : ones) {
            base.get(mask).add((short) (s & mask));
        }
        Map<Short, List<Short>> next = new ConcurrentHashMap<>();
        List<MaskedShort> result = Collections.synchronizedList(new ArrayList<>());

        for (int it = 0; it < nBits; it++) {
            logger.debug("Computing size-%,d prime implicants", 1 << it);

            logger.debug(
                    "Initial size: %,d divided into %,d groups",
                    base.values().stream().mapToInt(List::size).sum(), base.size());

            final List<Future<List<MaskedShort>>> tasks = new ArrayList<>();

            for (final short m : base.keySet()) {
                final List<Short> elementsWithSameMask = base.get(m);
                final int length = elementsWithSameMask.size();
                final boolean[] used = new boolean[length];

                final Map<Short, List<Short>> finalNext = next;
                final Map<Short, List<Short>> finalBase = base;

                final Future<List<MaskedShort>> task = executor.submit(() -> {
                    final List<MaskedShort> myResult = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        final short first = elementsWithSameMask.get(i);
                        for (int j = i + 1; j < length; j++) {
                            final short second = elementsWithSameMask.get(j);

                            final short diff = (short) (first ^ second);

                            if (BitUtils.has_one_bit(diff)) {
                                final short newMask = (short) (m & (~diff));
                                final short newValue = (short) (first & newMask);

                                if (!finalNext.containsKey(newMask)) {
                                    final List<Short> newList = Collections.synchronizedList(new ArrayList<>());
                                    newList.add(newValue);
                                    finalNext.put(newMask, newList);
                                } else {
                                    final List<Short> ls = finalNext.get(newMask);
                                    ls.add(newValue);
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
                                    new MaskedShort((short) (finalBase.get(m).get(i) & m), m);
                            myResult.add(toBeAdded);
                            logger.debug(
                                    "The value 0x%04x with mask 0x%04x was not used, adding %s to the result",
                                    finalBase.get(m).get(i), m, toBeAdded);
                        }
                    }

                    return myResult;
                });

                tasks.add(task);
            }

            for (final Future<List<MaskedShort>> t : tasks) {
                final List<MaskedShort> localResult;
                try {
                    localResult = t.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                result.addAll(localResult);
            }
            tasks.clear();

            logger.debug(
                    "next size: %,d",
                    next.values().stream().mapToInt(List::size).sum());
            logger.debug("Result size: %,d", result.size());

            base = new ConcurrentHashMap<>();
            for (final short m : next.keySet()) {
                // TODO: check if the HashSet is useful
                base.put(m, Collections.synchronizedList(new ArrayList<>(new HashSet<>(next.get(m)))));
            }
            next = new ConcurrentHashMap<>();
        }

        result = Collections.synchronizedList(new ArrayList<>(new HashSet<>(result)));

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
        final int bitsToFind = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!chart[r][c]) {
                    continue;
                }

                int count = 0;
                for (int i = 0; i < rows; i++) {
                    count += chart[i][c] ? 1 : 0;
                }

                if (count == bitsToFind) {
                    return r;
                }
            }
        }
        return -1;
    }
}

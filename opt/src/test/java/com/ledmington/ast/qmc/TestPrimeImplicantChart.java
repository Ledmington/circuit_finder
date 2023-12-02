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
package com.ledmington.ast.qmc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.ledmington.qmc.PrimeImplicantChart;
import com.ledmington.utils.MiniLogger;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

final class TestPrimeImplicantChart {

    private static Executable wrap(final Runnable task) {
        return task::run;
    }

    private static Stream<Arguments> invalidCases() {
        return Stream.of(
                Arguments.of(IllegalArgumentException.class, wrap(() -> new PrimeImplicantChart(0, 1))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new PrimeImplicantChart(1, 0))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> new PrimeImplicantChart(0, 0))),
                Arguments.of(IllegalArgumentException.class, wrap(() -> {
                    final PrimeImplicantChart pic = new PrimeImplicantChart(2, 2);
                    pic.set(-1, 0, true);
                })),
                Arguments.of(IllegalArgumentException.class, wrap(() -> {
                    final PrimeImplicantChart pic = new PrimeImplicantChart(2, 2);
                    pic.set(2, 0, true);
                })),
                Arguments.of(IllegalArgumentException.class, wrap(() -> {
                    final PrimeImplicantChart pic = new PrimeImplicantChart(2, 2);
                    pic.set(0, -1, true);
                })),
                Arguments.of(IllegalArgumentException.class, wrap(() -> {
                    final PrimeImplicantChart pic = new PrimeImplicantChart(2, 2);
                    pic.set(0, 2, true);
                })));
    }

    @ParameterizedTest
    @MethodSource("invalidCases")
    void expectThrow(final Class<Throwable> exceptionClass, final Executable task) {
        assertThrows(exceptionClass, task);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void string(int n) {
        final PrimeImplicantChart pic = new PrimeImplicantChart(n, n);
        assertEquals(Stream.generate(() -> "0".repeat(n)).limit(n).collect(Collectors.joining("\n")), pic.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void singleImplicant(int n) {
        MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.DEBUG);
        final PrimeImplicantChart pic = new PrimeImplicantChart(n, n);
        final int i = RandomGeneratorFactory.getDefault().create().nextInt(0, n);
        final int j = RandomGeneratorFactory.getDefault().create().nextInt(0, n);
        pic.set(i, j, true);
        assertEquals(List.of(i), pic.findPrimeImplicants());
    }

    private static Stream<Arguments> multipleImplicants() {
        final int maxNumberOfImplicants = 6;
        final int maxNumberOfChartsPerImplicant = 5;
        final int maxSize = Math.max(maxNumberOfImplicants, 10);
        final RandomGenerator rng = RandomGeneratorFactory.getDefault().create(System.nanoTime());
        final List<List<Integer>> inputs = new ArrayList<>();
        final List<PrimeImplicantChart> pics = new ArrayList<>();

        for (int numberOfImplicants = 2; numberOfImplicants <= maxNumberOfImplicants; numberOfImplicants++) {
            for (int chartIdx = 0; chartIdx < maxNumberOfChartsPerImplicant; chartIdx++) {
                // we want a chart with exactly n implicants
                // so we need at least n rows and at least n columns
                final int rows = rng.nextInt(numberOfImplicants, maxSize);
                final int columns = rng.nextInt(numberOfImplicants, maxSize);

                // select random rows to be prime implicants
                final List<Integer> primeImplicantRows = Stream.generate(() -> rng.nextInt(0, rows))
                        .distinct()
                        .limit(numberOfImplicants)
                        .toList();

                // select random columns where to place the 1
                final List<Integer> primeImplicantColumns = Stream.generate(() -> rng.nextInt(0, columns))
                        .distinct()
                        .limit(numberOfImplicants)
                        .toList();

                final PrimeImplicantChart pic = new PrimeImplicantChart(rows, columns);

                // add the prime implicants
                for (int i = 0; i < numberOfImplicants; i++) {
                    pic.set(primeImplicantRows.get(i), primeImplicantColumns.get(i), true);
                }

                // add random ones in the chart
                final int additionalOnes =
                        rng.nextInt(0, Math.max(1, rows * columns - numberOfImplicants * numberOfImplicants));
                for (int i = 0; i < additionalOnes; i++) {
                    final int r = rng.nextInt(0, rows);
                    final int c = rng.nextInt(0, columns);
                    // we add the random 1 either on a row which already has a prime implicant
                    // or we add it on another row and another column
                    if (!primeImplicantRows.contains(r) || primeImplicantColumns.contains(c)) {
                        continue;
                    }
                    pic.set(r, c, true);
                }

                inputs.add(primeImplicantRows);
                pics.add(pic);
            }
        }

        return IntStream.range(0, inputs.size()).mapToObj(i -> Arguments.of(inputs.get(i), pics.get(i)));
    }

    @ParameterizedTest
    @MethodSource("multipleImplicants")
    void multipleImplicants(final List<Integer> solution, final PrimeImplicantChart pic) {
        assertEquals(new HashSet<>(solution), new HashSet<>(pic.findPrimeImplicants()));
    }
}

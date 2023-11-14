/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast.qmc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
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
}

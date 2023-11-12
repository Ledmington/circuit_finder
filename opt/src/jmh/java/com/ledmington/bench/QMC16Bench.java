/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.bench;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ledmington.qmc.QMC16_V1;
import com.ledmington.qmc.QMC16_V2;
import com.ledmington.utils.MaskedShort;
import com.ledmington.utils.MiniLogger;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 5)
public class QMC16Bench {

    static {
        MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.ERROR);
    }

    private static final int inputBits = 6;
    private static List<Short> input;
    private static final QMC16_V1 v1 = new QMC16_V1();
    private static final QMC16_V2 v2 = new QMC16_V2(1);

    @Setup(Level.Iteration)
    public void setup() {
        input = new ArrayList<>(1 << inputBits);
        for (int i = 0; i < (1 << inputBits); i++) {
            input.add(i, (short) i);
        }
    }

    @Benchmark
    public void original(final Blackhole bh) {
        final List<MaskedShort> result = v1.minimize(inputBits, input);
        bh.consume(result);
    }

    @Benchmark
    public void sameMaskOpt(final Blackhole bh) {
        final List<MaskedShort> result = v2.minimize(inputBits, input);
        bh.consume(result);
    }
}

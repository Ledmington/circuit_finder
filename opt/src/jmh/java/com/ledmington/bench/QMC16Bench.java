/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2024 Filippo Barbari <filippo.barbari@gmail.com>
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
package com.ledmington.bench;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ledmington.qmc.QMC16_V1;
import com.ledmington.qmc.QMC16_V2;
import com.ledmington.qmc.QMC16_V3;
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
    private static final QMC16_V3 v3 = new QMC16_V3(1);

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

    @Benchmark
    public void vectorizedShortList(final Blackhole bh) {
        final List<MaskedShort> result = v3.minimize(inputBits, input);
        bh.consume(result);
    }
}

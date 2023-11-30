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
package com.ledmington.bench;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Timeout(time = 5)
public class MiniLoggerBench {

    private static final RandomGenerator rng =
            RandomGeneratorFactory.getDefault().create(System.nanoTime());

    private static String sampleString;
    private static final MiniLogger logger = MiniLogger.getLogger("bench");
    private static final PrintStream oldStdout = System.out;
    private static final OutputStream customOutputStream = new OutputStream() {
        private String s = "";

        @Override
        public void write(int b) {
            s += String.valueOf((byte) (b & 0x000000ff));
        }

        @Override
        public void flush() {
            s = "";
        }
    };
    private static final PrintStream customPrintStream = new PrintStream(customOutputStream);
    private static final PrintWriter customPrintWriter = new PrintWriter(customOutputStream);

    @Setup(Level.Iteration)
    public void beforeEachIteration() {
        System.setOut(customPrintStream);
        MiniLogger.setWriter(customPrintWriter);
        MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.DEBUG);
    }

    @Setup(Level.Invocation)
    public void beforeEachInvocation() {
        sampleString = String.valueOf(rng.nextDouble());
    }

    @TearDown(Level.Iteration)
    public void afterEachIteration() {
        try {
            customOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.setOut(oldStdout);
    }

    @Benchmark
    public void minilogger() {
        logger.debug(sampleString);
    }

    @Benchmark
    public void systemOut() {
        System.out.println(sampleString);
    }
}

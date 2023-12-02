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
package com.ledmington;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ledmington.ast.Optimizer;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.VariableNode;
import com.ledmington.function.BitArray;
import com.ledmington.function.LogicAnd;
import com.ledmington.function.LogicFunction;
import com.ledmington.function.LogicNand;
import com.ledmington.function.LogicNor;
import com.ledmington.function.LogicNot;
import com.ledmington.function.LogicOr;
import com.ledmington.function.SignedSum;
import com.ledmington.qmc.QMC16;
import com.ledmington.qmc.QMC16_V2;
import com.ledmington.utils.FormatUtils;
import com.ledmington.utils.Generators;
import com.ledmington.utils.ImmutableMap;
import com.ledmington.utils.MaskedShort;
import com.ledmington.utils.MiniLogger;
import com.ledmington.utils.TerminalCursor;
import com.ledmington.utils.TerminalCursor.TerminalColor;

public final class Main {

    private static final MiniLogger logger = MiniLogger.getLogger("app");
    private static final int MAX_BITS_SUPPORTED = 64;

    private static final String HELP_MSG = String.join("\n", new String[] {
        "",
        " --- Mandatory --- ",
        "     --bits N             Generates a circuit setting the variable number of bits to N",
        "     --operation OPNAME   Generates a circuit which computes the operation corresponding to OPNAME",
        "",
        "     --op_list            Prints the list of available operations and exits",
        "",
        " --- Output --- ",
        " -q, --quiet    Prints only errors",
        " -v             Prints errors and warnings",
        " -vv            Prints errors, warnings and info",
        " -vvv           Prints all the information",
        "",
        " --- Others --- ",
        " -h, --help     Prints this message and exits",
        " -j, --jobs N   Uses N threads (default: 1)",
        ""
    });

    private static final String OP_LIST = String.join("\n", new String[] {
        "   OP_NAME            BITS           DESCRIPTION",
        " logic_not           N -> N      Bitwise NOT.",
        " logic_and         2*N -> N      Bitwise AND.",
        " logic_or          2*N -> N      Bitwise OR.",
        " logic_nand        2*N -> N      Bitwise NAND.",
        " logic_nor         2*N -> N      Bitwise NOR.",
        " signed_sum        2*N -> N      Sum of signed integers.",
        ""
    });

    private static final ImmutableMap<String, LogicFunction> nameToOperation =
            ImmutableMap.<String, LogicFunction>builder()
                    .put("logic_not", new LogicNot())
                    .put("logic_and", new LogicAnd())
                    .put("logic_or", new LogicOr())
                    .put("logic_nand", new LogicNand())
                    .put("logic_nor", new LogicNor())
                    .put("signed_sum", new SignedSum())
                    .build();

    private static void computeCorrelationMatrix(
            final BigInteger limit, int inputBits, int outputBits, final LogicFunction op) {
        System.out.println();
        System.out.println("Computing correlation matrix...");

        // Copied and adapted from https://stackoverflow.com/a/28428582
        final double[] sx = new double[inputBits]; // zero-initialized
        final double[] sy = new double[outputBits]; // zero-initialized
        final double[] sxx = new double[inputBits]; // zero-initialized
        final double[] syy = new double[outputBits]; // zero-initialized
        final double[][] sxy = new double[inputBits][outputBits]; // zero-initialized

        Generators.bitStrings(inputBits).forEach(x -> {
            final BitArray in = new BitArray(x);
            final BitArray result = op.apply(in);

            for (int i = 0; i < inputBits; i++) {
                sx[i] += in.get(i) ? 1.0 : 0.0;
                sxx[i] += in.get(i) ? 1.0 : 0.0;
            }
            for (int i = 0; i < outputBits; i++) {
                sy[i] += result.get(i) ? 1.0 : 0.0;
                syy[i] += result.get(i) ? 1.0 : 0.0;
            }
            for (int i = 0; i < inputBits; i++) {
                for (int j = 0; j < outputBits; j++) {
                    sxy[i][j] += (in.get(i) ? 1.0 : 0.0) * (result.get(j) ? 1.0 : 0.0);
                }
            }
        });

        final double n = limit.doubleValue();
        final String correlationFormat = "  %4.2f  ";
        System.out.print("          ");
        for (int j = 0; j < outputBits; j++) {
            System.out.printf("out[%,2d] ", j);
        }
        System.out.println();
        for (int i = 0; i < inputBits; i++) {
            System.out.printf("in[%,3d] | ", i);
            for (int j = 0; j < outputBits; j++) {
                // covariance/covariation
                final double cov = sxy[i][j] / n - sx[i] * sy[j] / n / n;
                // standard error of x
                final double sigmax = Math.sqrt(sxx[i] / n - sx[i] * sx[i] / n / n);
                // standard error of y
                final double sigmay = Math.sqrt(syy[j] / n - sy[j] * sy[j] / n / n);

                // correlation is just a normalized covariation
                final double corr = cov / sigmax / sigmay;

                if (corr < -0.5) {
                    System.out.print(TerminalCursor.color(String.format(correlationFormat, corr), TerminalColor.RED));
                } else if (corr > 0.5) {
                    System.out.print(TerminalCursor.color(String.format(correlationFormat, corr), TerminalColor.GREEN));
                } else {
                    System.out.printf(correlationFormat, corr);
                }
            }
            System.out.println();
        }

        System.out.println();
    }

    private static void buildCircuit(int inputBits, int outputBits, final LogicFunction op, int nJobs) {
        System.out.println();
        System.out.println("Building circuit...\n");

        // FIXME: allow more than 26 variables
        if (inputBits > 26) {
            throw new IllegalArgumentException("Currently we do not support more than 26 different variables.");
        }

        if (inputBits > 16) {
            logger.error("Not yet available Quine-McCluskey for more than 16 bits");
            System.exit(-1);
        }

        for (int i = 0; i < outputBits; i++) {
            // Inputs for which the i-th output bit is 1
            final List<Short> ones = new ArrayList<>();
            final int limit = 1 << inputBits;

            for (int j = 0; j < limit; j++) {
                final BitArray in = new BitArray(inputBits);
                for (int k = 0; k < inputBits; k++) {
                    in.set(k, (j & (1 << k)) != 0);
                }
                final BitArray out = op.apply(in);

                if (out.get(i)) {
                    ones.add((short) j);
                }
            }

            final List<VariableNode> variables = new ArrayList<>();
            for (int k = 0; k < inputBits; k++) {
                variables.add(new VariableNode(String.valueOf((char) ('A' + k))));
            }

            final QMC16 qmc = new QMC16_V2(nJobs);
            final List<MaskedShort> result = qmc.minimize(inputBits, ones);

            final List<Node> tmp = new ArrayList<>();
            for (final MaskedShort ms : result) {
                final List<Node> ttmp = new ArrayList<>();
                for (int k = 0; k < inputBits; k++) {
                    if (ms.isRelevant(k)) {
                        ttmp.add(ms.isSet(k) ? variables.get(k) : new NotNode(variables.get(k)));
                    }
                }
                tmp.add(Node.and(ttmp));
            }
            final Node ast = Node.or(tmp);

            logger.info("Bit n.%,d qmc16 optimized circuit: '%s'\n", i, ast);

            logger.info(
                    "The boolean expression for bit %,d has %,d characters (AST representation: %,d nodes)\n",
                    i, ast.toString().length(), ast.size());

            final Node optimized = new Optimizer().optimize(ast);

            logger.info("Bit n.%,d ast optimized circuit: '%s'\n", i, optimized);
        }
    }

    public static void main(final String[] args) {
        MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.WARNING);
        int nJobs = 1;
        int bits = -1;
        String operation = "";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                case "--help":
                    System.out.println(HELP_MSG);
                    System.exit(0);
                    break;
                case "-j":
                case "--jobs":
                    try {
                        nJobs = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.err.printf("The parameter '--jobs' needs an integer, not '%s'.\n", args[i + 1]);
                        System.exit(-1);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("The parameter '--jobs' needs an integer, but none was found.");
                        System.exit(-1);
                    }
                    if (nJobs < 1) {
                        System.err.printf("Invalid value for '--jobs'. Should have been >= 1, but was %,d.\n", nJobs);
                    }
                    i++;
                    break;
                case "-q":
                case "--quiet":
                    MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.ERROR);
                    break;
                case "-v":
                    MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.WARNING);
                    break;
                case "-vv":
                    MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.INFO);
                    break;
                case "-vvv":
                    MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.DEBUG);
                    break;
                case "--bits":
                    try {
                        bits = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.err.printf("The parameter '--bits' needs an integer, not '%s'.\n", args[i + 1]);
                        System.exit(-1);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("The parameter '--bits' needs an integer, but none was found.");
                        System.exit(-1);
                    }
                    if (bits < 1 || bits > MAX_BITS_SUPPORTED) {
                        System.err.printf(
                                "Invalid value for '--bits'. Should have been between 1 and %,d but was %,d.\n",
                                MAX_BITS_SUPPORTED, bits);
                    }
                    i++;
                    break;
                case "--operation":
                    try {
                        operation = args[i + 1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println("The parameter '--operation' needs a string, but none was found.");
                        System.exit(-1);
                    }
                    i++;
                    break;
                case "--op_list":
                    System.out.println(OP_LIST);
                    System.exit(0);
                    break;
                default:
                    System.err.printf("\nUnknown parameter '%s'.\nQuitting.\n", args[i]);
                    System.exit(-1);
                    break;
            }
        }

        if (bits < 1) {
            System.err.println("Parameter '--bits' was not set.");
            System.exit(-1);
        }
        if (operation.isEmpty()) {
            System.err.println("Parameter '--operation' was not set.");
            System.exit(-1);
        }

        if (!nameToOperation.containsKey(operation)) {
            System.err.printf("Operation '%s' does not exist.\n", operation);
            System.exit(-1);
        }

        if (nJobs > Runtime.getRuntime().availableProcessors()) {
            logger.warning(
                    "Using more threads than available: %,d instead of %,d",
                    nJobs, Runtime.getRuntime().availableProcessors());
        }

        final LogicFunction op = nameToOperation.get(operation);
        final int inputBits = op.inputBits(bits);
        final int outputBits = op.outputBits(bits);
        final BigInteger limit = BigInteger.TWO.pow(inputBits);

        logger.info("Size of the dataset: 2^%,d (%s) elements\n", inputBits, FormatUtils.thousands(limit, ","));
        logger.info(
                "The selected operation requires %,d input bits to produce %,d output bits.\n", inputBits, outputBits);

        try {
            // computeCorrelationMatrix(limit, inputBits, outputBits, op);

            buildCircuit(inputBits, outputBits, op, nJobs);
            System.exit(0);
        } catch (Throwable t) {
            logger.error(t);
            System.exit(-1);
        }
    }
}

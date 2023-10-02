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
package com.ledmington;

import java.math.BigInteger;

import com.ledmington.utils.ImmutableMap;
import com.ledmington.utils.MiniLogger;

public final class Main {

    private static final int MAX_BITS_SUPPORTED = 64;

    private static final String HELP_MSG = String.join("\n", new String[] {
        "",
        " --- Mandatory --- ",
        "     --bits N             Generates a circuit setting the variable number of bits to N.",
        "     --operation OPNAME   Generates a circuit which computes the operation corresponding to OPNAME",
        "",
        "     --op_list            Prints the list of available operations and exits",
        "",
        " --- Output --- ",
        " -q, --quiet  Sets the verbosity to 0 (only errors)",
        " -v           Sets the verbosity to 1 (errors and warnings)",
        " -vv          Sets the verbosity to 2 (errors, warnings and info)",
        " -vvv         Sets the verbosity to 3 (no output is discarded)",
        "",
        " --- Others --- ",
        " -h, --help     Prints this message and exits.",
        " -j, --jobs N   Uses N threads.",
        ""
    });

    private static final String OP_LIST = String.join("\n", new String[] {
        "   OP_NAME          BITS          DESCRIPTION",
        "signed_sum        2*N -> N      Sum of signed integers.",
        "unsigned_sum      2*N -> N      Sum of unsigned integers.",
        "signed_sum_of     2*N -> N+1    Sum of signed integers with overflow checking.",
        "unsigned_sum_of   2*N -> N+1    Sum of unsigned integers with overflow checking.",
        ""
    });

    private static final ImmutableMap<String, LogicFunction> nameToOperation =
            ImmutableMap.<String, LogicFunction>builder()
                    .put("signed_sum", new SignedSum())
                    .build();

    public static void main(final String[] args) {
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
                        System.out.printf("The parameter '--jobs' needs an integer, not '%s'.\n", args[i + 1]);
                        System.exit(-1);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("The parameter '--jobs' needs an integer, but none was found.");
                        System.exit(-1);
                    }
                    if (nJobs < 1 || nJobs > Runtime.getRuntime().availableProcessors()) {
                        System.out.printf(
                                "Invalid value for '--jobs'. Should have been between 1 and %,d, but was %,d.\n",
                                Runtime.getRuntime().availableProcessors(), nJobs);
                    }
                    i++;
                    break;
                case "--bits":
                    try {
                        bits = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.out.printf("The parameter '--bits' needs an integer, not '%s'.\n", args[i + 1]);
                        System.exit(-1);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("The parameter '--bits' needs an integer, but none was found.");
                        System.exit(-1);
                    }
                    if (bits < 1 || bits > MAX_BITS_SUPPORTED) {
                        System.out.printf(
                                "Invalid value for '--bits'. Should have been between 1 and %,d but was %,d.\n",
                                MAX_BITS_SUPPORTED, bits);
                    }
                    i++;
                    break;
                case "--operation":
                    try {
                        operation = args[i + 1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("The parameter '--operation' needs a string, but none was found.");
                        System.exit(-1);
                    }
                    i++;
                    break;
                case "--op_list":
                    System.out.println(OP_LIST);
                    System.exit(0);
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

        if (!operation.equals("signed_sum")) {
            System.err.printf("Operation '%s' not supported at the moment, sorry.\n", operation);
            System.exit(0);
        }

        final BigInteger limit = BigInteger.TWO.pow(bits);
        final BigInteger mask = BigInteger.ONE.shiftLeft(bits / 2).subtract(BigInteger.ONE);
        for (BigInteger i = BigInteger.ZERO; i.compareTo(limit) < 0; i = i.add(BigInteger.ONE)) {
            final BitArray a = BitArray.convert(bits / 2, i.shiftRight(bits / 2));
            final BitArray b = BitArray.convert(bits / 2, i.and(mask));
            System.out.printf(
                    "%s + %s -> %s\n", a, b, nameToOperation.get(operation).apply(a));
        }
    }
}

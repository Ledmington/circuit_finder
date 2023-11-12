/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class TestNodeOrder extends TestOptimizer {

    private static Stream<Arguments> zeroOrdering() {
        return Stream.of(
                        Arguments.of(zero(), 0),
                        Arguments.of(one(), -1),
                        Arguments.of(not(zero()), -1),
                        Arguments.of(not(one()), -1),
                        Arguments.of(A(), -1),
                        Arguments.of(not(A()), -1),
                        Arguments.of(and(zero(), A()), -1),
                        Arguments.of(or(zero(), A()), -1))
                // duplicating each sample to test both A < B and B > A
                .flatMap(arg -> Stream.of(
                        Arguments.of(zero(), arg.get()[0], arg.get()[1]),
                        Arguments.of(arg.get()[0], zero(), -(Integer) arg.get()[1])));
    }

    @ParameterizedTest
    @MethodSource("zeroOrdering")
    void zeroOrdering(final Node a, final Node b, final int ordering) {
        assertEquals(
                ordering,
                a.compareTo(b),
                String.format(
                        "Wrong ordering between nodes %s and %s: should have been %d but was %d",
                        a, b, ordering, a.compareTo(b)));
    }

    private static Stream<Arguments> oneOrdering() {
        return Stream.of(
                        Arguments.of(zero(), 1),
                        Arguments.of(one(), 0),
                        Arguments.of(not(zero()), -1),
                        Arguments.of(not(one()), -1),
                        Arguments.of(A(), -1),
                        Arguments.of(not(A()), -1),
                        Arguments.of(and(zero(), A()), -1),
                        Arguments.of(or(zero(), A()), -1))
                // duplicating each sample to test both A < B and B > A
                .flatMap(arg -> Stream.of(
                        Arguments.of(one(), arg.get()[0], arg.get()[1]),
                        Arguments.of(arg.get()[0], one(), -(Integer) arg.get()[1])));
    }

    @ParameterizedTest
    @MethodSource("oneOrdering")
    void oneOrdering(final Node a, final Node b, int ordering) {
        assertEquals(
                ordering,
                a.compareTo(b),
                String.format(
                        "Wrong ordering between nodes %s and %s: should have been %d but was %d",
                        a, b, ordering, a.compareTo(b)));
    }

    private static Stream<Arguments> varOrdering() {
        return Stream.of(
                        Arguments.of(zero(), A()),
                        Arguments.of(one(), A()),
                        Arguments.of(A(), not(zero())),
                        Arguments.of(A(), not(one())),
                        Arguments.of(A(), B()),
                        Arguments.of(B(), C()),
                        Arguments.of(C(), D()),
                        Arguments.of(A(), not(A())),
                        Arguments.of(A(), and(zero(), A())),
                        Arguments.of(A(), or(zero(), A())))
                // duplicating each sample to test both A < B and B > A
                .flatMap(arg -> Stream.of(
                        Arguments.of(arg.get()[0], arg.get()[1], -1), Arguments.of(arg.get()[1], arg.get()[0], 1)));
    }

    @ParameterizedTest
    @MethodSource("varOrdering")
    void varOrdering(final Node before, final Node after, int ordering) {
        assertEquals(
                before.compareTo(after), ordering, String.format("Node %s should come before node %s", before, after));
    }

    private static Stream<Arguments> notOrdering() {
        return Stream.of(
                        Arguments.of(zero(), not(zero())),
                        Arguments.of(one(), not(one())),
                        Arguments.of(A(), not(A())),
                        Arguments.of(not(A()), not(not(A()))),
                        Arguments.of(not(A()), and(A(), B())),
                        Arguments.of(not(A()), or(A(), B())))
                // duplicating each sample to test both A < B and B > A
                .flatMap(arg -> Stream.of(
                        Arguments.of(arg.get()[0], arg.get()[1], -1), Arguments.of(arg.get()[1], arg.get()[0], 1)));
    }

    @ParameterizedTest
    @MethodSource("notOrdering")
    void notOrdering(final Node before, final Node after, int ordering) {
        assertEquals(
                before.compareTo(after), ordering, String.format("Node %s should come before node %s", before, after));
    }

    private static Stream<Arguments> andOrdering() {
        return Stream.of(
                        Arguments.of(zero(), and(A(), B())),
                        Arguments.of(one(), and(A(), B())),
                        Arguments.of(A(), and(A(), B())),
                        Arguments.of(not(A()), and(A(), B())),
                        Arguments.of(and(A(), B()), and(B(), C())),
                        Arguments.of(and(A(), B()), or(A(), B())),
                        Arguments.of(and(A(), B()), and(A(), B(), C())),
                        Arguments.of(and(A(), B()), and(B(), B())))
                // duplicating each sample to test both A < B and B > A
                .flatMap(arg -> Stream.of(
                        Arguments.of(arg.get()[0], arg.get()[1], -1), Arguments.of(arg.get()[1], arg.get()[0], 1)));
    }

    @ParameterizedTest
    @MethodSource("andOrdering")
    void andOrdering(final Node before, final Node after, int ordering) {
        assertEquals(
                before.compareTo(after), ordering, String.format("Node %s should come before node %s", before, after));
    }

    private static Stream<Arguments> orOrdering() {
        return Stream.of(
                        Arguments.of(zero(), or(A(), B())),
                        Arguments.of(one(), or(A(), B())),
                        Arguments.of(A(), or(A(), B())),
                        Arguments.of(not(A()), or(A(), B())),
                        Arguments.of(or(A(), B()), or(B(), C())),
                        Arguments.of(and(A(), B()), or(A(), B())),
                        Arguments.of(or(A(), B()), or(A(), B(), C())),
                        Arguments.of(or(A(), B()), or(B(), B())))
                // duplicating each sample to test both A < B and B > A
                .flatMap(arg -> Stream.of(
                        Arguments.of(arg.get()[0], arg.get()[1], -1), Arguments.of(arg.get()[1], arg.get()[0], 1)));
    }

    @ParameterizedTest
    @MethodSource("orOrdering")
    void orOrdering(final Node before, final Node after, int ordering) {
        assertEquals(
                before.compareTo(after), ordering, String.format("Node %s should come before node %s", before, after));
    }
}

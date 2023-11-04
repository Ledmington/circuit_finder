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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import com.ledmington.ast.nodes.Node;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestNodeOrder extends TestOptimizer {

    private static Stream<Arguments> zeroOrdering() {
        return Stream.of(
                Arguments.of(zero(), 0),
                Arguments.of(one(), -1),
                Arguments.of(not(zero()), -1),
                Arguments.of(not(one()), -1),
                Arguments.of(A(), -1),
                Arguments.of(not(A()), -1),
                Arguments.of(and(zero(), A()), -1),
                Arguments.of(or(zero(), A()), -1));
    }

    @ParameterizedTest
    @MethodSource("zeroOrdering")
    public void zeroOrdering(final Node n, int ordering) {
        assertEquals(
                ordering,
                zero().compareTo(n),
                String.format(
                        "Wrong ordering between nodes 0 and %s: should have been %d but was %d",
                        n, ordering, zero().compareTo(n)));
        assertEquals(
                -ordering,
                n.compareTo(zero()),
                String.format(
                        "Wrong ordering between nodes %s and 0: should have been %d but was %d",
                        n, -ordering, n.compareTo(zero())));
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
                Arguments.of(or(zero(), A()), -1));
    }

    @ParameterizedTest
    @MethodSource("oneOrdering")
    public void oneOrdering(final Node n, int ordering) {
        assertEquals(
                ordering,
                one().compareTo(n),
                String.format(
                        "Wrong ordering between nodes 1 and %s: should have been %d but was %d",
                        n, ordering, one().compareTo(n)));
        assertEquals(
                -ordering,
                n.compareTo(one()),
                String.format(
                        "Wrong ordering between nodes %s and 1: should have been %d but was %d",
                        n, -ordering, n.compareTo(one())));
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
                Arguments.of(A(), or(zero(), A())));
    }

    @ParameterizedTest
    @MethodSource("varOrdering")
    public void varOrdering(final Node before, final Node after) {
        assertTrue(before.compareTo(after) < 0, String.format("Node %s should come before node %s", before, after));
        assertTrue(after.compareTo(before) > 0, String.format("Node %s should come after node %s", after, before));
    }

    private static Stream<Arguments> notOrdering() {
        return Stream.of(
                Arguments.of(zero(), not(zero())),
                Arguments.of(one(), not(one())),
                Arguments.of(A(), not(A())),
                Arguments.of(not(A()), not(not(A()))),
                Arguments.of(not(A()), and(A(), B())),
                Arguments.of(not(A()), or(A(), B())));
    }

    @ParameterizedTest
    @MethodSource("notOrdering")
    public void notOrdering(final Node before, final Node after) {
        assertTrue(before.compareTo(after) < 0, String.format("Node %s should come before node %s", before, after));
        assertTrue(after.compareTo(before) > 0, String.format("Node %s should come after node %s", after, before));
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
                Arguments.of(and(A(), B()), and(B(), B())));
    }

    @ParameterizedTest
    @MethodSource("andOrdering")
    public void andOrdering(final Node before, final Node after) {
        assertTrue(before.compareTo(after) < 0, String.format("Node %s should come before node %s", before, after));
        assertTrue(after.compareTo(before) > 0, String.format("Node %s should come after node %s", after, before));
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
                Arguments.of(or(A(), B()), or(B(), B())));
    }

    @ParameterizedTest
    @MethodSource("orOrdering")
    public void orOrdering(final Node before, final Node after) {
        assertTrue(before.compareTo(after) < 0, String.format("Node %s should come before node %s", before, after));
        assertTrue(after.compareTo(before) > 0, String.format("Node %s should come after node %s", after, before));
    }
}

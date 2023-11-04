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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.utils.ImmutableMap;
import com.ledmington.utils.MaskedShort;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class TestQMC extends TestOptimizer {

    private static Stream<Arguments> fourVariableCircuits() {
        return Stream.of(
                Arguments.of(and(A(), B(), C(), D()), and(A(), B(), C(), D())),
                Arguments.of(or(and(A(), B(), C(), D()), and(A(), B(), C(), not(D()))), and(A(), B(), C())),
                Arguments.of(or(and(A(), B(), C(), D()), and(not(A()), B(), C(), D())), and(B(), C(), D())),
                Arguments.of(or(A(), and(A(), B())), A()),
                Arguments.of(and(A(), or(A(), B())), A()),
                Arguments.of(or(A(), B(), and(B(), C(), D())), or(A(), B())),
                Arguments.of(
                        or(and(A(), not(B())), and(not(A()), B())),
                        or(and(A(), not(B())), and(not(A()), B()))), // XOR cannot be simplified
                Arguments.of(and(A(), or(B(), and(C(), D()))), or(and(A(), B()), and(A(), C(), D()))),
                Arguments.of(not(and(A(), not(B()), C(), not(D()))), or(not(A()), B(), not(C()), D())),
                Arguments.of(
                        or(and(B(), not(C()), not(D())), and(A(), not(B())), and(A(), C())),
                        or(and(B(), not(C()), not(D())), and(A(), not(B())), and(A(), C()))));
    }

    @ParameterizedTest
    @MethodSource("fourVariableCircuits")
    public void fourVariableCircuits(final Node before, final Node after) {
        // convert the AST into a truth table (an OR of ANDs)
        final List<Short> truthTable = new ArrayList<>();
        for (int i = 0; i < (1 << 4); i++) {
            final Map<String, Boolean> variableValues = ImmutableMap.<String, Boolean>builder()
                    .put("A", (i & 0x1) != 0)
                    .put("B", (i & 0x2) != 0)
                    .put("C", (i & 0x4) != 0)
                    .put("D", (i & 0x8) != 0)
                    .build();
            if (before.evaluate(variableValues)) {
                truthTable.add((short) i);
            }
        }

        final List<MaskedShort> result = QMC16.minimize(4, truthTable);

        // re-converting the result back to an AST
        final List<Node> tmp = new ArrayList<>();
        for (final MaskedShort ms : result) {
            final List<Node> ttmp = new ArrayList<>();
            if (ms.isRelevant(0)) {
                ttmp.add(ms.isSet(0) ? A() : not(A()));
            }
            if (ms.isRelevant(1)) {
                ttmp.add(ms.isSet(1) ? B() : not(B()));
            }
            if (ms.isRelevant(2)) {
                ttmp.add(ms.isSet(2) ? C() : not(C()));
            }
            if (ms.isRelevant(3)) {
                ttmp.add(ms.isSet(3) ? D() : not(D()));
            }
            tmp.add(ttmp.size() == 1 ? ttmp.get(0) : new AndNode(ttmp));
        }
        final Node ast = tmp.size() == 1 ? tmp.get(0) : new OrNode(tmp);
        assertEquals(after, ast);
    }
}

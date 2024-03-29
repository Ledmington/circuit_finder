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
package com.ledmington.ast.qmc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.ledmington.ast.TestOptimizer;
import com.ledmington.ast.nodes.Node;
import com.ledmington.qmc.QMC16;
import com.ledmington.utils.ImmutableMap;
import com.ledmington.utils.MaskedShort;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class TestQMC extends TestOptimizer {

    protected QMC16 qmc;

    public abstract void setup();

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
    void fourVariableCircuits(final Node before, final Node after) {
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

        final List<MaskedShort> result = qmc.minimize(4, truthTable);

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
            tmp.add(Node.and(ttmp));
        }
        final Node ast = Node.or(tmp);
        assertEquals(after, ast);
    }
}

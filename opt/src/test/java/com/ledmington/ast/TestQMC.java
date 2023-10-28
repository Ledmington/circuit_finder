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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public final class TestQMC extends TestOptimizer {

    private static Stream<Arguments> fourVariableCircuits() {
        return Stream.of(
                Arguments.of(and(A(), B(), C(), D()), and(A(), B(), C(), D())),
                Arguments.of(or(and(A(), B(), C(), D()), and(A(), B(), C(), not(D()))), and(A(), B(), C())));
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

        final List<QMC16.MaskedShort> result = QMC16.minimize(4, truthTable);

        // re-converting the result back to an AST
        final List<Node> tmp = new ArrayList<>();
        for (final QMC16.MaskedShort ms : result) {
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
            tmp.add(new AndNode(ttmp));
        }
        final Node ast = tmp.size() == 1 ? tmp.get(0) : new OrNode(tmp);
        assertEquals(after, ast);
    }
}

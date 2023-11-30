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
package com.ledmington.ast.qmc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ledmington.ast.nodes.Node;
import com.ledmington.qmc.QMC16;
import com.ledmington.qmc.QMC16_V1;
import com.ledmington.utils.ImmutableMap;
import com.ledmington.utils.MaskedShort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

final class TestQMC16V1 extends TestQMC {

    @Override
    @BeforeEach
    public void setup() {
        qmc = new QMC16_V1();
    }

    @ParameterizedTest
    @MethodSource("fourVariableCircuits")
    void parallelization(final Node before, final Node after) {
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

        final QMC16 qmc1 = new QMC16_V1();
        final QMC16 qmc2 = new QMC16_V1();
        final List<MaskedShort> result1 = qmc1.minimize(4, truthTable);
        final List<MaskedShort> result2 = qmc2.minimize(4, truthTable);

        assertEquals(result1, result2);
    }
}

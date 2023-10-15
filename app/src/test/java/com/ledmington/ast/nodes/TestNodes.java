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
package com.ledmington.ast.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestNodes {

    @Test
    public void invalidBrackets() {
        assertThrows(NullPointerException.class, () -> new BracketsNode(null));
    }

    @Test
    public void invalidNot() {
        assertThrows(NullPointerException.class, () -> new NotNode(null));
    }

    @Test
    public void invalidAnd() {
        assertThrows(NullPointerException.class, () -> new AndNode(null));
        assertThrows(IllegalArgumentException.class, () -> new AndNode(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new AndNode(List.of(new ZeroNode())));
    }

    @Test
    public void invalidOr() {
        assertThrows(NullPointerException.class, () -> new OrNode(null));
        assertThrows(IllegalArgumentException.class, () -> new OrNode(List.of()));
        assertThrows(IllegalArgumentException.class, () -> new OrNode(List.of(new ZeroNode())));
    }

    @Test
    public void invalidVariable() {
        assertThrows(NullPointerException.class, () -> new VariableNode(null));
        assertThrows(IllegalArgumentException.class, () -> new VariableNode(""));
        assertThrows(IllegalArgumentException.class, () -> new VariableNode(" "));
    }

    @Test
    public void andEqualsIgnoreOrder() {
        assertEquals(
                new AndNode(List.of(new ZeroNode(), new OneNode())),
                new AndNode(List.of(new OneNode(), new ZeroNode())));
    }

    @Test
    public void orEqualsIgnoreOrder() {
        assertEquals(
                new OrNode(List.of(new ZeroNode(), new OneNode())), new OrNode(List.of(new OneNode(), new ZeroNode())));
    }
}

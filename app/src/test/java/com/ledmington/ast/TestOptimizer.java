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

import java.util.Arrays;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.BracketsNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.VariableNode;
import com.ledmington.ast.nodes.ZeroNode;

/**
 * This class exists only to provide common utility methods for testing.
 */
public abstract class TestOptimizer {

    protected static final Optimizer opt = new Optimizer(1);

    protected static Node zero() {
        return new ZeroNode();
    }

    protected static Node one() {
        return new OneNode();
    }

    protected static Node brackets(final Node n) {
        return new BracketsNode(n);
    }

    protected static Node not(final Node n) {
        return new NotNode(n);
    }

    protected static Node A() {
        return new VariableNode("A");
    }

    protected static Node B() {
        return new VariableNode("B");
    }

    protected static Node C() {
        return new VariableNode("C");
    }

    protected static Node or(final Node... nodes) {
        return new OrNode(Arrays.asList(nodes));
    }

    protected static Node and(final Node... nodes) {
        return new AndNode(Arrays.asList(nodes));
    }
}

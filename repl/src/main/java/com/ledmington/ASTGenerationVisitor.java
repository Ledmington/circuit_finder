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

import java.util.ArrayList;
import java.util.List;

import com.ledmington.ast.nodes.AndNode;
import com.ledmington.ast.nodes.Node;
import com.ledmington.ast.nodes.NotNode;
import com.ledmington.ast.nodes.OneNode;
import com.ledmington.ast.nodes.OrNode;
import com.ledmington.ast.nodes.VariableNode;
import com.ledmington.ast.nodes.ZeroNode;
import com.ledmington.gen.BooleanBaseVisitor;
import com.ledmington.gen.BooleanParser;

import org.antlr.v4.runtime.tree.ParseTree;

public final class ASTGenerationVisitor extends BooleanBaseVisitor<Node> {
    public Node visit(final ParseTree tree) {
        return super.visit(tree);
    }

    public Node visitExpr(final BooleanParser.ExprContext ctx) {
        if (ctx.ONE() != null) {
            return new OneNode();
        }
        if (ctx.ZERO() != null) {
            return new ZeroNode();
        }
        if (ctx.VAR() != null) {
            return new VariableNode(ctx.VAR().getText());
        }
        if (ctx.NOT() != null) {
            return new NotNode(visit(ctx.expr(0)));
        }
        if (ctx.OR() != null) {
            final List<Node> tmp = new ArrayList<>();
            for (int i = 0; i < ctx.OR().size() + 1; i++) {
                tmp.add(visit(ctx.expr(i)));
            }
            return new OrNode(tmp);
        }
        if (ctx.AND() != null) {
            final List<Node> tmp = new ArrayList<>();
            for (int i = 0; i < ctx.AND().size() + 1; i++) {
                tmp.add(visit(ctx.expr(i)));
            }
            return new AndNode(tmp);
        }
        if (ctx.LEFT_BRACKET() != null) {
            return visit(ctx.expr(0));
        }

        throw new RuntimeException(String.format("Unknown context '%s'", ctx));
    }
}

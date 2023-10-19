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

import java.util.Scanner;

import com.ledmington.ast.Optimizer;
import com.ledmington.ast.nodes.Node;
import com.ledmington.gen.BooleanLexer;
import com.ledmington.gen.BooleanParser;
import com.ledmington.utils.MiniLogger;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public final class Main {

    private static final MiniLogger logger = MiniLogger.getLogger("repl");

    private static void repl() {
        final Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print(">>> ");
            final String line = input.nextLine();
            if (line.isEmpty() || line.isBlank()) {
                continue;
            }

            final BooleanLexer lexer = new BooleanLexer(CharStreams.fromString(line));
            final BooleanParser parser = new BooleanParser(new CommonTokenStream(lexer));
            final ParseTree tree = parser.expr();
            final Node ast = new ASTGenerationVisitor().visit(tree);

            System.out.printf("Parsed: '%s' (size: %,d)\n", ast, ast.size());
            final Optimizer opt = new Optimizer(3);
            final Node optimized = opt.optimize(ast);
            System.out.printf("Optimized: '%s' (size: %,d)\n", optimized, optimized.size());
            System.out.println();
        }
    }

    public static void main(final String[] args) {
        MiniLogger.setMinimumLevel(MiniLogger.LoggingLevel.DEBUG);
        try {
            repl();
        } catch (Throwable t) {
            System.out.println();
            logger.error(t);
            System.exit(-1);
        }
    }
}

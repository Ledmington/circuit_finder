/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

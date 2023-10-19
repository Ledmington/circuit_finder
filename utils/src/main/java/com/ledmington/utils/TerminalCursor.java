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
package com.ledmington.utils;

/**
 * A class to format text on the terminal and to move the cursor around.
 */
public final class TerminalCursor {

    private static final boolean SUPPORTS_COLORS =
            System.getProperty("os.name").toLowerCase().contains("windows")
                    ||
                    // this check works only on Unix
                    (
                    /*System.console() != null && */ System.getenv().get("TERM") != null);
    private static final String RESET = "\u001b[0m";

    /**
     * An enum to map the most common terminal colors
     * with ANSI escape sequences.
     */
    public enum TerminalColor {

        /**
         * ANSI black color ("\u001b[30m").
         */
        BLACK(30),

        /**
         * ANSI red color ("\u001b[31m").
         */
        RED(31),

        /**
         * ANSI green color ("\u001b[32m").
         */
        GREEN(32),

        /**
         * ANSI yellow color ("\u001b[33m").
         */
        YELLOW(33),

        /**
         * ANSI blue color ("\u001b[34m").
         */
        BLUE(34),

        /**
         * ANSI magenta color ("\u001b[35m").
         */
        MAGENTA(35),

        /**
         * ANSI cyan color ("\u001b[36m").
         */
        CYAN(36),

        /**
         * ANSI white color ("\u001b[37m").
         */
        WHITE(37);

        private final int code;

        TerminalColor(final int code) {
            this.code = code;
        }

        /**
         * Returns the integer code to construct the ANSI escape sequence.
         *
         * @return
         *      The integer code.
         */
        public int getCode() {
            return this.code;
        }
    }

    /**
     * An enum to map the most common terminal "decorations"
     * with ANSI escape sequences.
     */
    public enum TerminalDecoration {

        /**
         * ANSI bold decoration ("\u001b[1m").
         */
        BOLD(1),

        /**
         * ANSI dim decoration ("\u001b[2m").
         */
        DIM(2),

        /**
         * ANSI italic decoration ("\u001b[3m").
         */
        ITALIC(3),

        /**
         * ANSI underlined decoration ("\u001b[4m").
         */
        UNDERLINED(4),

        /**
         * ANSI slow blink decoration ("\u001b[5m").
         */
        SLOW_BLINK(5),

        /**
         * ANSI rapid blink decoration ("\u001b[6m").
         */
        RAPID_BLINK(6),

        /**
         * ANSI reversed decoration ("\u001b[7m").
         */
        REVERSED(7),

        /**
         * ANSI striked decoration ("\u001b[9m").
         */
        STRIKED(9);

        private final int code;

        TerminalDecoration(final int code) {
            this.code = code;
        }

        /**
         * Returns the integer code to construct the ANSI escape sequence.
         *
         * @return
         *      The integer code.
         */
        public int getCode() {
            return this.code;
        }
    }

    private TerminalCursor() {}

    /**
     * Checks if the System.console() supports ANSI escape color sequences.
     *
     * @return
     *  true if it supports ANSI colors, false otherwise.
     */
    public static boolean supportsColors() {
        return SUPPORTS_COLORS;
    }

    /**
     * Colors and decorates the given text by wrapping it with the appropriate
     * ANSI escape sequences.
     *
     * @param text
     *      The text to color and/or decorate.
     * @param foreground
     *      The foregroud color.
     * @param decorations
     *      The decorations (underlined, bold, italic etc.).
     * @return
     *      The colored string.
     */
    public static String color(
            final String text, final TerminalColor foreground, final TerminalDecoration... decorations) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("\u001b[%dm", foreground.getCode()));
        for (TerminalDecoration decoration : decorations) {
            sb.append(String.format("\u001b[%dm", decoration.getCode()));
        }
        sb.append(text);
        sb.append(RESET);
        return sb.toString();
    }

    /**
     * Moves the cursor on the terminal by printing the appropriate ANSI escape sequence.
     *
     * @param columnOffset
     *      How many columns to move to. Positive to the right.
     * @param rowOffset
     *      How many rows to move to. Positive to the bottom.
     */
    public static void move(final int columnOffset, final int rowOffset) {
        final StringBuilder sb = new StringBuilder();
        if (columnOffset > 0) {
            sb.append(String.format("\u001b[%dC", columnOffset));
        } else if (columnOffset < 0) {
            sb.append(String.format("\u001b[%dD", columnOffset));
        }
        if (rowOffset > 0) {
            sb.append(String.format("\u001b[%dB", rowOffset));
        } else if (rowOffset < 0) {
            sb.append(String.format("\u001b[%dA", rowOffset));
        }
        System.out.print(sb.toString());
    }

    /**
     * Clears the screen.
     */
    public static void clearScreen() {
        // use '0J' to clear from the cursor to the end of the screen
        // use '1J' to clearn from cursor to the beginning of the screen
        // use '2J' to clear the whole screen
        System.out.print("\u001b[2J");
    }

    /**
     * Clears teh current line.
     */
    public static void clearLine() {
        // use '0K' to clear from the cursor to the end of the line
        // use '1K' to clearn from cursor to the beginning of the line
        // use '2K' to clear the whole line
        System.out.print("\u001b[2K");
    }
}

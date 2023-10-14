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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Personal implementation of a simple Logger
 * that mimics the behavior of the {@link java.util.logging.Logger} class.
 */
public final class MiniLogger {

    private static final Map<String, MiniLogger> ALL_LOGGERS = new HashMap<>();
    private static final long BEGINNING = System.nanoTime();
    private static final Map<LoggingLevel, TerminalCursor.TerminalColor> TAG_COLORS =
            ImmutableMap.<LoggingLevel, TerminalCursor.TerminalColor>builder()
                    .put(LoggingLevel.WARNING, TerminalCursor.TerminalColor.YELLOW)
                    .put(LoggingLevel.ERROR, TerminalCursor.TerminalColor.RED)
                    .build();
    private static LoggingLevel minimumLevel = LoggingLevel.DEBUG;

    /**
     * Specifies the level for all MiniLoggers.
     */
    public enum LoggingLevel {
        /**
         * The lowest level, useful for testing and debugging.
         */
        DEBUG,

        /**
         * Generally used for logging basic stuff.
         */
        INFO,

        /**
         * Useful for logging unexpected changes at runtime.
         */
        WARNING,

        /**
         * Generally used for critical errors and exceptions.
         */
        ERROR
    }

    /**
     * Returns a new MiniLogger with the given name.
     *
     * @param name
     *      The name of the MiniLogger.
     * @return
     *      A new MiniLogger instance.
     */
    public static MiniLogger getLogger(final String name) {
        Objects.requireNonNull(name);
        if (!ALL_LOGGERS.containsKey(name)) {
            ALL_LOGGERS.put(name, new MiniLogger(name));
        }
        return ALL_LOGGERS.get(name);
    }

    /**
     * Sets the minimum logging level for all MiniLogger instances.
     *
     * @param level
     *      The new logging level.
     */
    public static void setMinimumLevel(final LoggingLevel level) {
        Objects.requireNonNull(level);
        minimumLevel = level;
    }

    /**
     * Returns the currently set minimum logging level.
     *
     * @return
     *      The minimum logging level.
     */
    public static LoggingLevel getLoggingLevel() {
        return minimumLevel;
    }

    private final String name;

    private MiniLogger(final String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    private void log(final String formatString, final LoggingLevel tag, final Object... args) {
        Objects.requireNonNull(formatString);
        Objects.requireNonNull(tag);
        long t = System.nanoTime() - BEGINNING;
        t /= 1_000_000;
        final long milliseconds = t % 1000;
        t /= 1000;
        final long seconds = t % 60;
        t /= 60;
        final long minutes = t % 60;
        t /= 60;
        final long hours = t % 24;

        final String coloredHeader = String.format(
                "[%02d:%02d:%02d.%03d][%s][%s][%s]",
                hours,
                minutes,
                seconds,
                milliseconds,
                Thread.currentThread().getName(),
                name,
                TerminalCursor.color(tag.name(), TAG_COLORS.getOrDefault(tag, TerminalCursor.TerminalColor.WHITE)));

        String coloredLine = String.format(
                "%s " + formatString,
                Stream.concat(Stream.of(coloredHeader), Arrays.stream(args)).toArray());

        coloredLine = coloredLine.replace("\n", String.format("\n%s ", coloredHeader));

        synchronized (this) {
            // printing on console
            System.out.println(coloredLine);
        }
    }

    /**
     * Logs a message with logging level DEBUG.
     *
     * @param formatString
     *      The string to be formatted.
     * @param args
     *      The arguments to be placed inside the string.
     */
    public void debug(final String formatString, final Object... args) {
        if (minimumLevel != LoggingLevel.DEBUG) {
            return;
        }
        log(formatString, LoggingLevel.DEBUG, args);
    }

    /**
     * Logs a message with logging level INFO.
     *
     * @param formatString
     *      The string to be formatted.
     * @param args
     *      The arguments to be placed inside the string.
     */
    public void info(final String formatString, final Object... args) {
        if (minimumLevel == LoggingLevel.WARNING || minimumLevel == LoggingLevel.ERROR) {
            return;
        }
        log(formatString, LoggingLevel.INFO, args);
    }

    /**
     * Logs a message with logging level WARNING.
     *
     * @param formatString
     *      The string to be formatted.
     * @param args
     *      The arguments to be placed inside the string.
     */
    public void warning(final String formatString, final Object... args) {
        if (minimumLevel == LoggingLevel.ERROR) {
            return;
        }
        log(formatString, LoggingLevel.WARNING, args);
    }

    /**
     * Logs a message with logging level ERROR.
     *
     * @param formatString
     *      The string to be formatted.
     * @param args
     *      The arguments to be placed inside the string.
     */
    public void error(final String formatString, final Object... args) {
        log(formatString, LoggingLevel.ERROR, args);
    }

    /**
     * Logs the given Throwable with logging level ERROR.
     *
     * @param t
     *      The Throwable to be logged.
     */
    public void error(final Throwable t) {
        error(t.getClass().getName());
        if (t.getMessage() != null) {
            error(t.getMessage());
        } else {
            error("(no message)");
        }
        for (final StackTraceElement ste : t.getStackTrace()) {
            error("  " + ste.toString());
        }
        if (t.getCause() != null) {
            error("Caused by:");
            error(t.getCause());
        }
    }
}

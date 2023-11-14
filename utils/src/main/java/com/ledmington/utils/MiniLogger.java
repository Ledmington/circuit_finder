/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.utils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private static PrintWriter stdout = System.console() == null
            ? new PrintWriter(System.out)
            : System.console().writer();
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
         * Generally used for critical errors and exceptions. Cannot be disabled.
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

    /**
     * Allows to change Where the output will go.
     *
     * @param pw
     *      A non-null PrintWriter. Defaults to System.console().writer() if available,
     *      otherwise System.out.
     */
    public static void setWriter(final PrintWriter pw) {
        stdout = Objects.requireNonNull(pw);
    }

    private final String loggerName;

    private MiniLogger(final String name) {
        Objects.requireNonNull(name);
        this.loggerName = name;
    }

    private String getFormattedTime() {
        long t = System.nanoTime() - BEGINNING;
        t /= 1_000_000;
        final long milliseconds = t % 1000;
        t /= 1000;
        final long seconds = t % 60;
        t /= 60;
        final long minutes = t % 60;
        t /= 60;
        final long hours = t % 24;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    private String getFormattedHeader(final LoggingLevel tag) {
        Objects.requireNonNull(tag);

        return '['
                + getFormattedTime()
                + ']'
                + '['
                + Thread.currentThread().getName()
                + ']'
                + '['
                + loggerName
                + ']'
                + '['
                + TerminalCursor.color(tag.name(), TAG_COLORS.getOrDefault(tag, TerminalCursor.TerminalColor.WHITE))
                + ']';
    }

    private void outputActual(final String line) {
        synchronized (this) {
            // printing on console
            stdout.println(line);
        }
    }

    private void log(final String msg, final LoggingLevel tag) {
        Objects.requireNonNull(msg);

        final String coloredHeader = getFormattedHeader(tag);

        final StringBuilder sb = new StringBuilder(coloredHeader + ' ');
        for (final char c : msg.toCharArray()) {
            sb.append(c);
            if (c == '\n') {
                sb.append(coloredHeader).append(' ');
            }
        }

        outputActual(sb.toString());
    }

    /**
     * Logs a message with logging level DEBUG.
     *
     * @param msg
     *      The string to be printed.
     */
    public void debug(final String msg) {
        if (minimumLevel != LoggingLevel.DEBUG) {
            return;
        }
        log(msg, LoggingLevel.DEBUG);
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
        log(String.format(formatString, args), LoggingLevel.DEBUG);
    }

    /**
     * Logs a message with logging level INFO.
     *
     * @param msg
     *      The string to be printed.
     */
    public void info(final String msg) {
        if (minimumLevel == LoggingLevel.WARNING || minimumLevel == LoggingLevel.ERROR) {
            return;
        }
        log(msg, LoggingLevel.INFO);
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
        log(String.format(formatString, args), LoggingLevel.INFO);
    }

    /**
     * Logs a message with logging level WARNING.
     *
     * @param msg
     *      The string to be printed.
     */
    public void warning(final String msg) {
        if (minimumLevel == LoggingLevel.ERROR) {
            return;
        }
        log(msg, LoggingLevel.WARNING);
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
        log(String.format(formatString, args), LoggingLevel.WARNING);
    }

    /**
     * Logs a message with logging level ERROR.
     *
     * @param msg
     *      The string to be printed.
     */
    public void error(final String msg) {
        log(msg, LoggingLevel.ERROR);
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
        log(String.format(formatString, args), LoggingLevel.ERROR);
    }

    /**
     * Logs the given Throwable with logging level ERROR.
     *
     * @param t
     *      The Throwable to be logged.
     */
    @SuppressWarnings("PMD.GuardLogStatement")
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

/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.utils;

import java.math.BigInteger;

public final class FormatUtils {
    private FormatUtils() {}

    public static String thousands(final BigInteger x, final String separator) {
        final String s = x.toString();
        final StringBuilder sb = new StringBuilder();

        final int digitsLength = s.length() - (Character.isDigit(s.charAt(0)) ? 0 : 1);
        int i = 0;
        for (; i < digitsLength; i++) {
            sb.append(s.charAt(s.length() - 1 - i));
            if (digitsLength > 3 && i % 3 == 2 && i != digitsLength - 1) {
                sb.append(separator);
            }
        }
        if (i < s.length()) {
            // appending the sign
            sb.append(s.charAt(0));
        }
        return sb.reverse().toString();
    }
}

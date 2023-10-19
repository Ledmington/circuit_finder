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

import java.math.BigInteger;

public final class FormatUtils {
    private FormatUtils() {}

    public static String thousands(BigInteger x, final String separator) {
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

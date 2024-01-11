/*
* circuit-finder - A search algorithm to find optimal logic circuits.
* Copyright (C) 2023-2024 Filippo Barbari <filippo.barbari@gmail.com>
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
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.qmc;

import java.util.ArrayList;
import java.util.List;

import com.ledmington.utils.MiniLogger;

public final class PrimeImplicantChart {

    private static final MiniLogger logger = MiniLogger.getLogger("pic");

    private final int rows;
    private final int columns;
    private final boolean[][] m;
    private final boolean[] deletedRows;
    private final boolean[] deletedColumns;

    public PrimeImplicantChart(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            throw new IllegalArgumentException(String.format(
                    "Invalid number of rows and columns: should have been both >= 1 but were %,d and %,d",
                    rows, columns));
        }
        this.rows = rows;
        this.columns = columns;
        this.m = new boolean[rows][columns];
        this.deletedRows = new boolean[rows];
        this.deletedColumns = new boolean[columns];
        logger.debug("The prime implicant chart is %,dx%,d: %,d bytes", rows, columns, rows * columns + rows + columns);
    }

    private void assertRowIndexIsValid(int r) {
        if (r < 0 || r >= rows) {
            throw new IllegalArgumentException(
                    String.format("Invalid row index %,d for matrix %,dx%,d", r, rows, columns));
        }
    }

    private void assertColumnIndexIsValid(int c) {
        if (c < 0 || c >= columns) {
            throw new IllegalArgumentException(
                    String.format("Invalid column index %,d for matrix %,dx%,d", c, rows, columns));
        }
    }

    public void set(int rowIndex, int columnIndex, boolean value) {
        assertRowIndexIsValid(rowIndex);
        assertColumnIndexIsValid(columnIndex);
        if (deletedRows[rowIndex]) {
            throw new IllegalArgumentException(String.format("Cannot set value on deleted row %,d", rowIndex));
        }
        if (deletedColumns[columnIndex]) {
            throw new IllegalArgumentException(String.format("Cannot set value on deleted column %,d", columnIndex));
        }
        this.m[rowIndex][columnIndex] = value;
    }

    /**
     * A dominated row is a row which is entirely "contained" or "covered" by another row.
     * If Ri and Rj are two rows of the chart, we say that Ri dominates Rj
     * if and only if every '1' bit of Rj is also a '1' bit in Ri.
     */
    private void removeDominatedRows() {
        for (int i = 0; i < rows; i++) {
            if (deletedRows[i]) {
                continue;
            }
            for (int j = i + 1; j < rows; j++) {
                if (deletedRows[j]) {
                    continue;
                }

                // checking rows i and j
                boolean iDominatesJ = true;
                boolean jDominatesI = true;
                for (int k = 0; k < columns; k++) {
                    if (deletedColumns[k]) {
                        continue;
                    }
                    if (m[i][k] && !m[j][k]) {
                        jDominatesI = false;
                    }
                    if (m[j][k] && !m[i][k]) {
                        iDominatesJ = false;
                    }
                    if (!iDominatesJ && !jDominatesI) {
                        break;
                    }
                }
                if (iDominatesJ && jDominatesI) {
                    // the rows i and j, were equal, so we delete only j
                    deletedRows[j] = true;
                    logger.debug("Deleted row %,d: dominated by row %,d", j, i);
                } else if (iDominatesJ) {
                    // i dominates j, we delete j
                    deletedRows[j] = true;
                    logger.debug("Deleted row %,d: dominated by row %,d", j, i);
                } else if (jDominatesI) {
                    // j dominates i, we delete i
                    deletedRows[i] = true;
                    logger.debug("Deleted row %,d: dominated by row %,d", i, j);
                }
            }
        }
    }

    private int findEssentialPrimeImplicants() {
        final int bitsToFind = 1;
        for (int r = 0; r < rows; r++) {
            if (deletedRows[r]) {
                continue;
            }
            for (int c = 0; c < columns; c++) {
                if (deletedColumns[c]) {
                    continue;
                }

                int count = 0;
                for (int i = 0; i < rows; i++) {
                    if (deletedRows[i]) {
                        continue;
                    }
                    if (m[i][c]) {
                        count++;
                    }
                }

                if (count == bitsToFind) {
                    return r;
                }
            }
        }
        return -1;
    }

    public List<Integer> findPrimeImplicants() {
        removeDominatedRows();

        final List<Integer> result = new ArrayList<>();
        int epiIdx = findEssentialPrimeImplicants();

        while (epiIdx != -1) {
            result.add(epiIdx);
            logger.debug("Found essential prime implicant: row %,d", epiIdx);

            deletedRows[epiIdx] = true;
            for (int c = 0; c < columns; c++) {
                if (m[epiIdx][c]) {
                    deletedColumns[c] = true;
                }
            }

            removeDominatedRows();

            epiIdx = findEssentialPrimeImplicants();
        }

        return result;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean ignoreDeleted) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            if (ignoreDeleted && deletedRows[i]) {
                continue;
            }
            for (int j = 0; j < columns; j++) {
                if (ignoreDeleted && deletedColumns[j]) {
                    continue;
                }
                sb.append((m[i][j] && !deletedRows[i] && !deletedColumns[j]) ? '1' : '0');
            }
            if (i != rows - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}

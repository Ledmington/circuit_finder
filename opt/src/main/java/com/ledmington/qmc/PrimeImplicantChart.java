/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
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

    public PrimeImplicantChart(int r, int c) {
        if (r < 1 || c < 1) {
            throw new IllegalArgumentException(String.format(
                    "Invalid number of rows and columns: should have been both >= 1 but were %,d and %,d", r, c));
        }
        this.rows = r;
        this.columns = c;
        this.m = new boolean[r][c];
        this.deletedRows = new boolean[r];
        this.deletedColumns = new boolean[c];
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

    public void set(int r, int c, boolean value) {
        assertRowIndexIsValid(r);
        assertColumnIndexIsValid(c);
        if (deletedRows[r]) {
            throw new IllegalArgumentException(String.format("Cannot set value on deleted row %,d", r));
        }
        if (deletedColumns[c]) {
            throw new IllegalArgumentException(String.format("Cannot set value on deleted column %,d", c));
        }
        this.m[r][c] = value;
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

    /**
     * A dominated column is a column which is entirely "contained" or "covered" by another column.
     * If Ci and Cj are two columns of the chart, we say that Ci dominates Cj
     * if and only if every '1' bit of Cj is also a '1' bit in Ci.
     */
    private void removeDominatedColumns() {
        for (int i = 0; i < columns; i++) {
            if (deletedColumns[i]) {
                continue;
            }

            for (int j = i + 1; j < columns; j++) {
                if (deletedColumns[j]) {
                    continue;
                }

                // checking columns i and j
                boolean iDominatesJ = true;
                boolean jDominatesI = true;
                for (int k = 0; k < rows; k++) {
                    if (m[k][i] && !m[k][j]) {
                        jDominatesI = false;
                    }
                    if (m[k][j] && !m[k][i]) {
                        iDominatesJ = false;
                    }
                    if (!iDominatesJ && !jDominatesI) {
                        break;
                    }
                }
                if (iDominatesJ && jDominatesI) {
                    // the columns i and j, were equal, so we delete only j
                    deletedColumns[j] = true;
                    logger.debug("Deleted column %,d: dominated by column %,d", j, i);
                } else if (iDominatesJ) {
                    // i dominates j, we delete j
                    deletedColumns[j] = true;
                    logger.debug("Deleted column %,d: dominated by column %,d", j, i);
                } else if (jDominatesI) {
                    // j dominates i, we delete i
                    deletedColumns[i] = true;
                    logger.debug("Deleted column %,d: dominated by column %,d", i, j);
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
                    count += m[i][c] ? 1 : 0;
                }

                if (count == bitsToFind) {
                    return r;
                }
            }
        }
        return -1;
    }

    public List<Integer> findPrimeImplicants() {
        logger.debug("PIC before removing rows");
        logger.debug(this.toString(true));
        removeDominatedRows();
        logger.debug("PIC before removing columns");
        logger.debug(this.toString(true));
        removeDominatedColumns();
        logger.debug("PIC after removing columns");
        logger.debug(this.toString(true));

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

            logger.debug(this.toString(true));

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

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
import java.util.HashSet;
import java.util.List;

import com.ledmington.utils.BitUtils;
import com.ledmington.utils.MaskedShort;
import com.ledmington.utils.MiniLogger;

/**
 * Implementation of Quine-McCluskey algorithm (optimized for 16 bits).
 * <a href="https://www.tandfonline.com/doi/abs/10.1080/00029890.1952.11988183">Original paper</a>.
 * <p>
 * Note: use {@link com.ledmington.qmc.QMC16_V2} instead.
 */
public final class QMC16_V1 implements QMC16 {

    private static final MiniLogger logger = MiniLogger.getLogger("qmc16");

    public QMC16_V1() {
        logger.warning(
                "The class %s is deprecated: use QMC16_V2 instead",
                this.getClass().getName());
    }

    @Override
    public List<MaskedShort> minimize(final int nBits, final List<Short> ones) {
        if (nBits < 1 || nBits > 16) {
            throw new IllegalArgumentException(
                    String.format("Illegal number of bits: should have been between 1 and 16 but was %,d", nBits));
        }

        // placing a 0 where the bits are not relevant
        final short mask = (short) (0xffff >> (16 - nBits));
        logger.debug("nBits: %,d -> initial mask: 0x%04x", nBits, mask);
        if (BitUtils.popcount(mask) != nBits) {
            throw new RuntimeException(String.format(
                    "Wrong mask created: should have had %,d 1s but had %,d", nBits, BitUtils.popcount(mask)));
        }

        List<MaskedShort> base = new ArrayList<>();
        for (final short s : ones) {
            base.add(new MaskedShort((short) (s & mask), mask));
        }
        List<MaskedShort> next = new ArrayList<>();
        final List<MaskedShort> result = new ArrayList<>();

        for (int it = 0; it < nBits; it++) {
            logger.debug("Computing size-%,d prime implicants", 1 << it);

            logger.debug("Initial size: %,d", base.size());

            final int length = base.size();
            final boolean[] used = new boolean[length];

            for (int i = 0; i < length; i++) {
                final MaskedShort first = base.get(i);
                final short firstValue = first.value();
                final short firstMask = first.mask();

                for (int j = i + 1; j < length; j++) {
                    final MaskedShort second = base.get(j);
                    final short secondValue = second.value();
                    final short secondMask = second.mask();

                    if (firstMask != secondMask) {
                        continue;
                    }

                    final short diff = (short) (firstValue ^ secondValue);

                    if (BitUtils.has_one_bit(diff)) {
                        final short newMask = (short) (firstMask & (~diff));
                        final short newValue = (short) (firstValue & newMask);

                        next.add(new MaskedShort(newValue, newMask));

                        used[i] = true;
                        used[j] = true;
                    }
                }
            }

            for (int i = 0; i < length; i++) {
                if (used[i]) {
                    continue;
                }

                // This implicant was not used to compute the "next size" implicants.
                // Apply the mask before adding.
                final short val = base.get(i).value();
                final short m = base.get(i).mask();
                final MaskedShort toBeAdded = new MaskedShort((short) (val & m), m);
                result.add(toBeAdded);
                logger.debug(
                        "The value 0x%04x with mask 0x%04x was not used, adding %s to the result", val, m, toBeAdded);
            }

            logger.debug("next size: %,d", next.size());
            logger.debug("Result size: %,d", result.size());

            base = new ArrayList<>(new HashSet<>(next));
            next = new ArrayList<>();

            // result = new ArrayList<>(new HashSet<>(result));

            logger.debug("Result size: %,d", result.size());
        }

        // Building prime implicant chart
        final PrimeImplicantChart pic = new PrimeImplicantChart(result.size(), ones.size());
        logger.debug(
                "The prime implicant chart is %,dx%,d: %,d bytes",
                result.size(), ones.size(), result.size() * ones.size());
        for (int i = 0; i < result.size(); i++) {
            final short vi = result.get(i).value();
            final short mi = result.get(i).mask();
            for (int j = 0; j < ones.size(); j++) {
                pic.set(
                        i,
                        j,
                        // checking that the 1s are in the right place
                        (vi & mi & ones.get(j)) == vi
                                &&
                                // checking that the 0s are in the right place
                                (~(~vi & mi & ~ones.get(j)) & mi) == vi);
            }
        }

        return pic.findPrimeImplicants().stream().map(result::get).toList();
    }
}

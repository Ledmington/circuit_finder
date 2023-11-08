/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.qmc;

import java.util.List;

import com.ledmington.utils.MaskedShort;

public interface QMC16 {
    List<MaskedShort> minimize(int nBits, final List<Short> ones);
}

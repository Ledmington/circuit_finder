/*
 * Copyright (C) 2023-2023 Filippo Barbari <filippo.barbari@gmail.com>
 *
 * This file is part of circuit-finder.
 *
 * circuit-finder can not be copied and/or distributed without
 * the explicit permission of Filippo Barbari.
 */
package com.ledmington.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.DataflowAnomalyAnalysis"})
final class TestImmutableMap {
    @Test
    void initiallyEmpty() {
        final Map<String, Integer> m = new ImmutableMap<>();
        assertTrue(m.isEmpty(), String.format("Expected empty map but had size %,d.", m.size()));
    }

    @Test
    void sameSizeAsCopy() {
        final Map<String, Integer> m1 = Map.of("aaa", 1, "bbb", 2);
        final Map<String, Integer> m2 = new ImmutableMap<>(m1);
        assertEquals(m1.size(), m2.size(), String.format("Expected same size %,d but was %,d.", m1.size(), m2.size()));
    }

    @Test
    void cannotClear() {
        final Map<String, Integer> m = new ImmutableMap<>();
        assertThrows(UnsupportedOperationException.class, m::clear);
    }

    @Test
    void cannotPut() {
        final Map<String, Integer> m = new ImmutableMap<>();
        assertThrows(UnsupportedOperationException.class, () -> m.put("aaa", 1));
    }

    @Test
    void cannotPutAll() {
        final Map<String, Integer> m = new ImmutableMap<>();
        assertThrows(UnsupportedOperationException.class, () -> m.putAll(Map.of("aaa", 1, "bbb", 2)));
    }

    @Test
    void cannotRemove() {
        final Map<String, Integer> m =
                ImmutableMap.<String, Integer>builder().put("aaa", 1).build();
        assertThrows(UnsupportedOperationException.class, () -> m.remove("aaa"));
    }
}

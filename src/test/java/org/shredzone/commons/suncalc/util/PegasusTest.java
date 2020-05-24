/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2018 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.shredzone.commons.suncalc.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.function.Function;

import org.assertj.core.data.Offset;
import org.junit.Test;

/**
 * Unit tests for {@link Pegasus}.
 */
public class PegasusTest {

    private static final Offset<Double> ERROR = Offset.offset(0.001);

    @Test
    public void testParabola() {
        // f(x) = x^2 + 2x - 3
        // Roots at x = -3 and x = 1
        Function<Double, Double> parabola = x -> x * x + 2 * x - 3;

        double r1 = Pegasus.calculate(0.0, 3.0, 0.1, parabola);
        assertThat(r1).isCloseTo(1.0, ERROR);

        double r2 = Pegasus.calculate(-5.0, 0.0, 0.1, parabola);
        assertThat(r2).isCloseTo(-3.0, ERROR);

        try {
            Pegasus.calculate(-2.0, 0.5, 0.1, parabola);
            fail("Found a non-existing root");
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    @Test(expected = ArithmeticException.class)
    public void testParabola2() {
        // f(x) = x^2 + 3
        // No roots
        Pegasus.calculate(-5.0, 5.0, 0.1, x -> x * x + 3);
    }

}

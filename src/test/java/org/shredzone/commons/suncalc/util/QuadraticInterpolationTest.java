/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2017 Richard "Shred" Körber
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link QuadraticInterpolation}.
 */
public class QuadraticInterpolationTest {

    private static final double ERROR = 0.001;

    @Test
    public void testTwoRoots() {
        QuadraticInterpolation qi = new QuadraticInterpolation(1.0, -1.0, 1.0);

        assertThat(qi.getNumberOfRoots(), is(2));
        assertThat(qi.getRoot1(), is(closeTo(-0.707, ERROR)));
        assertThat(qi.getRoot2(), is(closeTo( 0.707, ERROR)));
        assertThat(qi.getXe(), is(closeTo( 0.0, ERROR)));
        assertThat(qi.getYe(), is(closeTo(-1.0, ERROR)));
    }

    @Test
    public void testOneRoot() {
        QuadraticInterpolation qi = new QuadraticInterpolation(2.0, 0.0, -1.0);

        assertThat(qi.getNumberOfRoots(), is(1));
        assertThat(qi.getRoot1(), is(closeTo( 0.0, ERROR)));
        assertThat(qi.getXe(), is(closeTo( 1.5, ERROR)));
        assertThat(qi.getYe(), is(closeTo(-1.125, ERROR)));
    }

    @Test
    public void testNoRoot() {
        QuadraticInterpolation qi = new QuadraticInterpolation(3.0, 2.0, 1.0);

        assertThat(qi.getNumberOfRoots(), is(0));
    }

}

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

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

/**
 * Unit tests for {@link QuadraticInterpolation}.
 */
public class QuadraticInterpolationTest {

    private static final Offset<Double> ERROR = Offset.offset(0.001);

    @Test
    public void testTwoRootsAndMinimum() {
        QuadraticInterpolation qi = new QuadraticInterpolation(1.0, -1.0, 1.0);

        assertThat(qi.getNumberOfRoots()).isEqualTo(2);
        assertThat(qi.getRoot1()).isCloseTo(-0.707, ERROR);
        assertThat(qi.getRoot2()).isCloseTo( 0.707, ERROR);
        assertThat(qi.getXe()).isCloseTo( 0.0, ERROR);
        assertThat(qi.getYe()).isCloseTo(-1.0, ERROR);
        assertThat(qi.isMaximum()).isFalse();
    }

    @Test
    public void testTwoRootsAndMaximum() {
        QuadraticInterpolation qi = new QuadraticInterpolation(-1.0, 1.0, -1.0);

        assertThat(qi.getNumberOfRoots()).isEqualTo(2);
        assertThat(qi.getRoot1()).isCloseTo(-0.707, ERROR);
        assertThat(qi.getRoot2()).isCloseTo( 0.707, ERROR);
        assertThat(qi.getXe()).isCloseTo(0.0, ERROR);
        assertThat(qi.getYe()).isCloseTo(1.0, ERROR);
        assertThat(qi.isMaximum()).isTrue();
    }

    @Test
    public void testOneRoot() {
        QuadraticInterpolation qi = new QuadraticInterpolation(2.0, 0.0, -1.0);

        assertThat(qi.getNumberOfRoots()).isEqualTo(1);
        assertThat(qi.getRoot1()).isCloseTo( 0.0, ERROR);
        assertThat(qi.getXe()).isCloseTo( 1.5, ERROR);
        assertThat(qi.getYe()).isCloseTo(-1.125, ERROR);
        assertThat(qi.isMaximum()).isFalse();
    }

    @Test
    public void testNoRoot() {
        QuadraticInterpolation qi = new QuadraticInterpolation(3.0, 2.0, 1.0);

        assertThat(qi.getNumberOfRoots()).isZero();
    }

}

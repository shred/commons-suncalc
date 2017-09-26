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

import static java.lang.Math.PI;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link Matrix}.
 */
public class MatrixTest {

    private static final double ERROR = 0.001;
    private static final double PI_HALF = PI / 2.0;

    @Test
    public void testIdentity() {
        Matrix mx = Matrix.identity();
        assertValues(mx,
                1.0, 0.0, 0.0,
                0.0, 1.0, 0.0,
                0.0, 0.0, 1.0);
    }

    @Test
    public void testRotateX() {
        Matrix mx = Matrix.rotateX(PI_HALF);
        assertValues(mx,
                1.0,  0.0,  0.0,
                0.0,  0.0,  1.0,
                0.0, -1.0,  0.0);
    }

    @Test
    public void testRotateY() {
        Matrix mx = Matrix.rotateY(PI_HALF);
        assertValues(mx,
                0.0,  0.0, -1.0,
                0.0,  1.0,  0.0,
                1.0,  0.0,  0.0);
    }

    @Test
    public void testRotateZ() {
        Matrix mx = Matrix.rotateZ(PI_HALF);
        assertValues(mx,
                0.0,  1.0,  0.0,
               -1.0,  0.0,  0.0,
                0.0,  0.0,  1.0);
    }

    @Test
    public void testTranspose() {
        Matrix mx = Matrix.rotateX(PI_HALF).transpose();
        assertValues(mx,
                1.0,  0.0,  0.0,
                0.0,  0.0, -1.0,
                0.0,  1.0,  0.0);
    }

    @Test
    public void testNegate() {
        Matrix mx = Matrix.identity().negate();
        assertValues(mx,
               -1.0,  0.0,  0.0,
                0.0, -1.0,  0.0,
                0.0,  0.0, -1.0);
    }

    @Test
    public void testAdd() {
        Matrix mx1 = Matrix.rotateX(PI_HALF);
        Matrix mx2 = Matrix.rotateY(PI_HALF);

        assertValues(mx1.add(mx2),
                1.0,  0.0, -1.0,
                0.0,  1.0,  1.0,
                1.0, -1.0,  0.0);

        assertValues(mx2.add(mx1),
                1.0,  0.0, -1.0,
                0.0,  1.0,  1.0,
                1.0, -1.0,  0.0);
    }

    @Test
    public void testSubtract() {
        Matrix mx1 = Matrix.rotateX(PI_HALF);
        Matrix mx2 = Matrix.rotateY(PI_HALF);

        assertValues(mx1.subtract(mx2),
                1.0,  0.0,  1.0,
                0.0, -1.0,  1.0,
               -1.0, -1.0,  0.0);

        assertValues(mx2.subtract(mx1),
               -1.0,  0.0, -1.0,
                0.0,  1.0, -1.0,
                1.0,  1.0,  0.0);
    }

    @Test
    public void testMultiply() {
        Matrix mx1 = Matrix.rotateX(PI_HALF);
        Matrix mx2 = Matrix.rotateY(PI_HALF);

        assertValues(mx1.multiply(mx2),
                0.0,  0.0, -1.0,
                1.0,  0.0,  0.0,
                0.0, -1.0,  0.0);

        assertValues(mx2.multiply(mx1),
                0.0,  1.0,  0.0,
                0.0,  0.0,  1.0,
                1.0,  0.0,  0.0);
    }

    @Test
    public void testScalarMultiply() {
        Matrix mx = Matrix.identity().multiply(5.0);
        assertValues(mx,
                5.0, 0.0, 0.0,
                0.0, 5.0, 0.0,
                0.0, 0.0, 5.0);
    }

    @Test
    public void testVectorMultiply() {
        Matrix mx = Matrix.rotateX(PI_HALF);
        Vector vc = new Vector(5.0, 8.0, -3.0);
        Vector result = mx.multiply(vc);
        assertThat(result.getX(), is(closeTo( 5.0, ERROR)));
        assertThat(result.getY(), is(closeTo(-3.0, ERROR)));
        assertThat(result.getZ(), is(closeTo(-8.0, ERROR)));
    }

    @Test
    public void testEquals() {
        Matrix mx1 = Matrix.identity();
        Matrix mx2 = Matrix.rotateX(PI_HALF);
        Matrix mx3 = Matrix.identity();

        assertThat(mx1.equals(mx2), is(false));
        assertThat(mx1.equals(mx3), is(true));
        assertThat(mx2.equals(mx3), is(false));
        assertThat(mx3.equals(mx1), is(true));
        assertThat(mx1.equals(null), is(false));
        assertThat(mx1.equals(new Object()), is(false));
    }

    @Test
    public void testHashCode() {
        int mx1 = Matrix.identity().hashCode();
        int mx2 = Matrix.rotateX(PI_HALF).hashCode();
        int mx3 = Matrix.identity().hashCode();

        assertThat(mx1, not(equalTo(0)));
        assertThat(mx2, not(equalTo(0)));
        assertThat(mx3, not(equalTo(0)));
        assertThat(mx1, not(equalTo(mx2)));
        assertThat(mx1, is(equalTo(mx3)));
    }

    @Test
    public void testToString() {
        Matrix mx = Matrix.identity();

        assertThat(mx.toString(), is("[[1.0, 0.0, 0.0], [0.0, 1.0, 0.0], [0.0, 0.0, 1.0]]"));
    }

    private void assertValues(Matrix mx, double... values) {
        for (int ix = 0; ix < values.length; ix++) {
            int r = ix / 3;
            int c = ix % 3;
            assertThat("r=" + r +", c=" + c,
                    mx.get(r, c), is(closeTo(values[ix], ERROR)));
        }
    }

}

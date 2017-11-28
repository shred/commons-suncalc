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
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.junit.Test;

/**
 * Unit tests for {@link Vector}.
 */
public class VectorTest {

    private static final Offset<Double> ERROR = Offset.offset(0.001);
    private static final double PI_HALF = PI / 2.0;

    @Test
    public void testConstructors() {
        Vector v1 = new Vector(20.0, 10.0, 5.0);
        assertThat(v1.getX()).isEqualTo(20.0);
        assertThat(v1.getY()).isEqualTo(10.0);
        assertThat(v1.getZ()).isEqualTo(5.0);

        Vector v2 = new Vector(new double[] { 20.0, 10.0, 5.0 });
        assertThat(v2.getX()).isEqualTo(20.0);
        assertThat(v2.getY()).isEqualTo(10.0);
        assertThat(v2.getZ()).isEqualTo(5.0);

        Vector v3 = Vector.ofPolar(0.5, 0.25);
        assertThat(v3.getPhi()).isEqualTo(0.5);
        assertThat(v3.getTheta()).isEqualTo(0.25);
        assertThat(v3.getR()).isEqualTo(1.0);

        Vector v4 = Vector.ofPolar(0.5, 0.25, 50.0);
        assertThat(v4.getPhi()).isEqualTo(0.5);
        assertThat(v4.getTheta()).isEqualTo(0.25);
        assertThat(v4.getR()).isEqualTo(50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstructor() {
        new Vector(new double[] { 20.0, 10.0 });
    }

    @Test
    public void testAdd() {
        Vector v1 = new Vector(20.0, 10.0, 5.0);
        Vector v2 = new Vector(10.0, 25.0, 15.0);

        Vector r1 = v1.add(v2);
        assertThat(r1.getX()).isEqualTo(30.0);
        assertThat(r1.getY()).isEqualTo(35.0);
        assertThat(r1.getZ()).isEqualTo(20.0);

        Vector r2 = v2.add(v1);
        assertThat(r2.getX()).isEqualTo(30.0);
        assertThat(r2.getY()).isEqualTo(35.0);
        assertThat(r2.getZ()).isEqualTo(20.0);
    }

    @Test
    public void testSubtract() {
        Vector v1 = new Vector(20.0, 10.0, 5.0);
        Vector v2 = new Vector(10.0, 25.0, 15.0);

        Vector r1 = v1.subtract(v2);
        assertThat(r1.getX()).isEqualTo(10.0);
        assertThat(r1.getY()).isEqualTo(-15.0);
        assertThat(r1.getZ()).isEqualTo(-10.0);

        Vector r2 = v2.subtract(v1);
        assertThat(r2.getX()).isEqualTo(-10.0);
        assertThat(r2.getY()).isEqualTo(15.0);
        assertThat(r2.getZ()).isEqualTo(10.0);
    }

    @Test
    public void testMultiply() {
        Vector v1 = new Vector(20.0, 10.0, 5.0);

        Vector r1 = v1.multiply(5.0);
        assertThat(r1.getX()).isEqualTo(100.0);
        assertThat(r1.getY()).isEqualTo(50.0);
        assertThat(r1.getZ()).isEqualTo(25.0);
    }

    @Test
    public void testNegate() {
        Vector v1 = new Vector(20.0, 10.0, 5.0);

        Vector r1 = v1.negate();
        assertThat(r1.getX()).isEqualTo(-20.0);
        assertThat(r1.getY()).isEqualTo(-10.0);
        assertThat(r1.getZ()).isEqualTo(-5.0);
    }

    @Test
    public void testCross() {
        Vector v1 = new Vector(3.0, -3.0, 1.0);
        Vector v2 = new Vector(4.0, 9.0, 2.0);

        Vector r1 = v1.cross(v2);
        assertThat(r1.getX()).isEqualTo(-15.0);
        assertThat(r1.getY()).isEqualTo(-2.0);
        assertThat(r1.getZ()).isEqualTo(39.0);
    }

    @Test
    public void testDot() {
        Vector v1 = new Vector(1.0, 2.0, 3.0);
        Vector v2 = new Vector(4.0, -5.0, 6.0);

        double r1 = v1.dot(v2);
        assertThat(r1).isCloseTo(12.0, ERROR);
    }

    @Test
    public void testNorm() {
        Vector v1 = new Vector(5.0, -6.0, 7.0);

        double r1 = v1.norm();
        assertThat(r1).isCloseTo(10.488, ERROR);
    }

    @Test
    public void testEquals() {
        Vector v1 = new Vector(3.0, -3.0, 1.0);
        Vector v2 = new Vector(4.0, 9.0, 2.0);
        Vector v3 = new Vector(3.0, -3.0, 1.0);

        assertThat(v1.equals(v2)).isFalse();
        assertThat(v1.equals(v3)).isTrue();
        assertThat(v2.equals(v3)).isFalse();
        assertThat(v3.equals(v1)).isTrue();
        assertThat(v1.equals(null)).isFalse();
        assertThat(v1.equals(new Object())).isFalse();
    }

    @Test
    public void testHashCode() {
        int h1 = new Vector(3.0, -3.0, 1.0).hashCode();
        int h2 = new Vector(4.0, 9.0, 2.0).hashCode();
        int h3 = new Vector(3.0, -3.0, 1.0).hashCode();

        assertThat(h1).isNotZero();
        assertThat(h2).isNotZero();
        assertThat(h3).isNotZero();
        assertThat(h1).isNotEqualTo(h2);
        assertThat(h1).isEqualTo(h3);
    }

    @Test
    public void testToString() {
        Vector v1 = new Vector(3.0, -3.0, 1.0);

        assertThat(v1.toString()).isEqualTo("(x=3.0, y=-3.0, z=1.0)");
    }

    @Test
    public void testToCartesian() {
        Vector v1 = Vector.ofPolar(0.0, 0.0);
        assertThat(v1.getX()).isCloseTo(1.0, ERROR);
        assertThat(v1.getY()).isCloseTo(0.0, ERROR);
        assertThat(v1.getZ()).isCloseTo(0.0, ERROR);

        Vector v2 = Vector.ofPolar(PI_HALF, 0.0);
        assertThat(v2.getX()).isCloseTo(0.0, ERROR);
        assertThat(v2.getY()).isCloseTo(1.0, ERROR);
        assertThat(v2.getZ()).isCloseTo(0.0, ERROR);

        Vector v3 = Vector.ofPolar(0.0, PI_HALF);
        assertThat(v3.getX()).isCloseTo(0.0, ERROR);
        assertThat(v3.getY()).isCloseTo(0.0, ERROR);
        assertThat(v3.getZ()).isCloseTo(1.0, ERROR);

        Vector v4 = Vector.ofPolar(PI_HALF, PI_HALF);
        assertThat(v4.getX()).isCloseTo(0.0, ERROR);
        assertThat(v4.getY()).isCloseTo(0.0, ERROR);
        assertThat(v4.getZ()).isCloseTo(1.0, ERROR);

        Vector v5 = Vector.ofPolar(PI_HALF, -PI_HALF);
        assertThat(v5.getX()).isCloseTo(0.0, ERROR);
        assertThat(v5.getY()).isCloseTo(0.0, ERROR);
        assertThat(v5.getZ()).isCloseTo(-1.0, ERROR);

        Vector v6 = Vector.ofPolar(0.0, 0.0, 5.0);
        assertThat(v6.getX()).isCloseTo(5.0, ERROR);
        assertThat(v6.getY()).isCloseTo(0.0, ERROR);
        assertThat(v6.getZ()).isCloseTo(0.0, ERROR);

        Vector v7 = Vector.ofPolar(PI_HALF, 0.0, 5.0);
        assertThat(v7.getX()).isCloseTo(0.0, ERROR);
        assertThat(v7.getY()).isCloseTo(5.0, ERROR);
        assertThat(v7.getZ()).isCloseTo(0.0, ERROR);

        Vector v8 = Vector.ofPolar(0.0, PI_HALF, 5.0);
        assertThat(v8.getX()).isCloseTo(0.0, ERROR);
        assertThat(v8.getY()).isCloseTo(0.0, ERROR);
        assertThat(v8.getZ()).isCloseTo(5.0, ERROR);

        Vector v9 = Vector.ofPolar(PI_HALF, PI_HALF, 5.0);
        assertThat(v9.getX()).isCloseTo(0.0, ERROR);
        assertThat(v9.getY()).isCloseTo(0.0, ERROR);
        assertThat(v9.getZ()).isCloseTo(5.0, ERROR);

        Vector v10 = Vector.ofPolar(PI_HALF, -PI_HALF, 5.0);
        assertThat(v10.getX()).isCloseTo(0.0, ERROR);
        assertThat(v10.getY()).isCloseTo(0.0, ERROR);
        assertThat(v10.getZ()).isCloseTo(-5.0, ERROR);
    }

    @Test
    public void testToPolar() {
        Vector v1 = new Vector(20.0, 0.0, 0.0);
        assertThat(v1.getPhi()).isEqualTo(0.0);
        assertThat(v1.getTheta()).isEqualTo(0.0);
        assertThat(v1.getR()).isEqualTo(20.0);

        Vector v2 = new Vector(0.0, 20.0, 0.0);
        assertThat(v2.getPhi()).isEqualTo(PI_HALF);
        assertThat(v2.getTheta()).isEqualTo(0.0);
        assertThat(v2.getR()).isEqualTo(20.0);

        Vector v3 = new Vector(0.0, 0.0, 20.0);
        assertThat(v3.getPhi()).isEqualTo(0.0);
        assertThat(v3.getTheta()).isEqualTo(PI_HALF);
        assertThat(v3.getR()).isEqualTo(20.0);

        Vector v4 = new Vector(-20.0, 0.0, 0.0);
        assertThat(v4.getPhi()).isEqualTo(PI);
        assertThat(v4.getTheta()).isEqualTo(0.0);
        assertThat(v4.getR()).isEqualTo(20.0);

        Vector v5 = new Vector(0.0, -20.0, 0.0);
        assertThat(v5.getPhi()).isEqualTo(PI + PI_HALF);
        assertThat(v5.getTheta()).isEqualTo(0.0);
        assertThat(v5.getR()).isEqualTo(20.0);

        Vector v6 = new Vector(0.0, 0.0, -20.0);
        assertThat(v6.getPhi()).isEqualTo(0.0);
        assertThat(v6.getTheta()).isEqualTo(-PI_HALF);
        assertThat(v6.getR()).isEqualTo(20.0);

        Vector v7 = new Vector(0.0, 0.0, 0.0);
        assertThat(v7.getPhi()).isEqualTo(0.0);
        assertThat(v7.getTheta()).isEqualTo(0.0);
        assertThat(v7.getR()).isEqualTo(0.0);
    }

}

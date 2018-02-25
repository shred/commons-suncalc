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

import static java.lang.Math.*;

import java.util.Arrays;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * A three dimensional matrix.
 * <p>
 * Objects are immutable and threadsafe.
 */
@ParametersAreNonnullByDefault
@Immutable
public class Matrix {

    private final double[] mx;

    private Matrix() {
        mx = new double[9];
    }

    private Matrix(double... values) {
        if (values == null || values.length != 9) {
            throw new IllegalArgumentException("requires 9 values");
        }
        mx = values;
    }

    /**
     * Creates an identity matrix.
     *
     * @return Identity {@link Matrix}
     */
    public static Matrix identity() {
        return new Matrix(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0);
    }

    /**
     * Creates a matrix that rotates a vector by the given angle at the X axis.
     *
     * @param angle
     *            angle, in radians
     * @return Rotation {@link Matrix}
     */
    public static Matrix rotateX(double angle) {
        double s = sin(angle);
        double c = cos(angle);
        return new Matrix(
            1.0, 0.0, 0.0,
            0.0,   c,   s,
            0.0,  -s,   c
        );
    }

    /**
     * Creates a matrix that rotates a vector by the given angle at the Y axis.
     *
     * @param angle
     *            angle, in radians
     * @return Rotation {@link Matrix}
     */
    public static Matrix rotateY(double angle) {
        double s = sin(angle);
        double c = cos(angle);
        return new Matrix(
              c, 0.0,  -s,
            0.0, 1.0, 0.0,
              s, 0.0,   c
        );
    }

    /**
     * Creates a matrix that rotates a vector by the given angle at the Z axis.
     *
     * @param angle
     *            angle, in radians
     * @return Rotation {@link Matrix}
     */
    public static Matrix rotateZ(double angle) {
        double s = sin(angle);
        double c = cos(angle);
        return new Matrix(
              c,   s, 0.0,
             -s,   c, 0.0,
            0.0, 0.0, 1.0
        );
    }

    /**
     * Transposes this matrix.
     *
     * @return {@link Matrix} that is a transposition of this matrix.
     */
    public Matrix transpose() {
        Matrix result = new Matrix();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.set(i, j, get(j, i));
            }
        }
        return result;
    }

    /**
     * Negates this matrix.
     *
     * @return {@link Matrix} that is a negation of this matrix.
     */
    public Matrix negate() {
        Matrix result = new Matrix();
        for (int i = 0; i < 9; i++) {
            result.mx[i] = -mx[i];
        }
        return result;
    }

    /**
     * Adds a matrix to this matrix.
     *
     * @param right
     *            {@link Matrix} to add
     * @return {@link Matrix} that is a sum of both matrices
     */
    public Matrix add(Matrix right) {
        Matrix result = new Matrix();
        for (int i = 0; i < 9; i++) {
            result.mx[i] = mx[i] + right.mx[i];
        }
        return result;
    }

    /**
     * Subtracts a matrix from this matrix.
     *
     * @param right
     *            {@link Matrix} to subtract
     * @return {@link Matrix} that is the difference of both matrices
     */
    public Matrix subtract(Matrix right) {
        Matrix result = new Matrix();
        for (int i = 0; i < 9; i++) {
            result.mx[i] = mx[i] - right.mx[i];
        }
        return result;
    }

    /**
     * Multiplies two matrices.
     *
     * @param right
     *            {@link Matrix} to multiply with
     * @return {@link Matrix} that is the product of both matrices
     */
    public Matrix multiply(Matrix right) {
        Matrix result = new Matrix();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double scalp = 0.0;
                for (int k = 0; k < 3; k++) {
                    scalp += get(i, k) * right.get(k, j);
                }
                result.set(i, j, scalp);
            }
        }
        return result;
    }

    /**
     * Performs a scalar multiplication.
     *
     * @param scalar
     *            Scalar to multiply with
     * @return {@link Matrix} that is the scalar product
     */
    public Matrix multiply(double scalar) {
        Matrix result = new Matrix();
        for (int i = 0; i < 9; i++) {
            result.mx[i] = mx[i] * scalar;
        }
        return result;
    }

    /**
     * Applies this matrix to a {@link Vector}.
     *
     * @param right
     *            {@link Vector} to multiply with
     * @return {@link Vector} that is the product of this matrix and the given vector
     */
    public Vector multiply(Vector right) {
        double[] vec = new double[] {right.getX(), right.getY(), right.getZ()};
        double[] result = new double[3];

        for (int i = 0; i < 3; i++) {
            double scalp = 0.0;
            for (int j = 0; j < 3; j++) {
                scalp += get(i, j) * vec[j];
            }
            result[i] = scalp;
        }

        return new Vector(result);
    }

    /**
     * Gets a value from the matrix.
     *
     * @param r
     *            Row number (0..2)
     * @param c
     *            Column number (0..2)
     * @return Value at that position
     */
    public double get(int r, int c) {
        if (r < 0 || r > 2 || c < 0 || c > 2) {
            throw new IllegalArgumentException("row/column out of range: " + r + ":" + c);
        }
        return mx[r * 3 + c];
    }

    /**
     * Changes a value in the matrix. As a {@link Matrix} object is immutable from the
     * outside, this method is private.
     *
     * @param r
     *            Row number (0..2)
     * @param c
     *            Column number (0..2)
     * @param v
     *            New value
     */
    private void set(int r, int c, double v) {
        if (r < 0 || r > 2 || c < 0 || c > 2) {
            throw new IllegalArgumentException("row/column out of range: " + r + ":" + c);
        }
        mx[r * 3 + c] = v;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Matrix)) {
            return false;
        }
        return Arrays.equals(mx, ((Matrix) obj).mx);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mx);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int ix = 0; ix < 9; ix++) {
            if (ix % 3 == 0) {
                sb.append('[');
            }
            sb.append(mx[ix]);
            if (ix % 3 == 2) {
                sb.append(']');
            }
            if (ix < 8) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }

}

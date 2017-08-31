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

/**
 * A three dimensional matrix.
 * <p>
 * Objects are immutable and threadsafe.
 */
public class Matrix {

    private final double[][] mx = new double[3][3];

    private Matrix() {
        // use zero matrix
    }

    private Matrix(double d00, double d01, double d02, //NOSONAR: performance reasons
            double d10, double d11, double d12,
            double d20, double d21, double d22) {
        mx[0][0] = d00;
        mx[0][1] = d01;
        mx[0][2] = d02;
        mx[1][0] = d10;
        mx[1][1] = d11;
        mx[1][2] = d12;
        mx[2][0] = d20;
        mx[2][1] = d21;
        mx[2][2] = d22;
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
                result.mx[i][j] = mx[j][i];
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.mx[i][j] = -mx[i][j];
            }
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.mx[i][j] = mx[i][j] + right.mx[i][j];
            }
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.mx[i][j] = mx[i][j] - right.mx[i][j];
            }
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
                    scalp += mx[i][k] * right.mx[k][j];
                }
                result.mx[i][j] = scalp;
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result.mx[i][j] = mx[i][j] * scalar;
            }
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
                scalp += mx[i][j] * vec[j];
            }
            result[i] = scalp;
        }

        return new Vector(result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Matrix)) {
            return false;
        }
        return Arrays.deepEquals(mx, ((Matrix) obj).mx);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(mx);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(mx);
    }

}

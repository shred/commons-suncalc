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
import static org.shredzone.commons.suncalc.util.ExtendedMath.*;

/**
 * A three dimensional vector.
 * <p>
 * Objects are is immutable and threadsafe.
 */
public class Vector {

    private final double x;
    private final double y;
    private final double z;
    private boolean hasPolar = false;
    private double φ;
    private double θ;
    private double r;

    /**
     * Creates a new {@link Vector} of the given cartesian coordinates.
     *
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     * @param z
     *            Z coordinate
     */
    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a new {@link Vector} of the given cartesian coordinates.
     *
     * @param d
     *            Array of coordinates, must have 3 elements
     */
    public Vector(double[] d) {
        if (d.length != 3) {
            throw new IllegalArgumentException("invalid vector length");
        }
        this.x = d[0];
        this.y = d[1];
        this.z = d[2];
    }

    /**
     * Creates a new {@link Vector} of the given polar coordinates, with a radial distance
     * of 1.
     *
     * @param φ
     *            Azimuthal Angle
     * @param θ
     *            Polar Angle
     * @return Created {@link Vector}
     */
    public static Vector ofPolar(double φ, double θ) {
        return ofPolar(φ, θ, 1.0);
    }

    /**
     * Creates a new {@link Vector} of the given polar coordinates.
     *
     * @param φ
     *            Azimuthal Angle
     * @param θ
     *            Polar Angle
     * @param r
     *            Radial Distance
     * @return Created {@link Vector}
     */
    public static Vector ofPolar(double φ, double θ, double r) {
        double cosθ = cos(θ);
        Vector result = new Vector(
            r * cos(φ) * cosθ,
            r * sin(φ) * cosθ,
            r *          sin(θ)
        );

        result.φ = φ;
        result.θ = θ;
        result.r = r;
        result.hasPolar = true;

        return result;
    }

    /**
     * Returns the cartesian X coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the cartesian Y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the cartesian Z coordinate.
     */
    public double getZ() {
        return z;
    }

    /**
     * Returns the azimuthal angle (φ) in radians.
     */
    public double getPhi() {
        computePolar();
        return φ;
    }

    /**
     * Returns the polar angle (θ) in radians.
     */
    public double getTheta() {
        computePolar();
        return θ;
    }

    /**
     * Returns the polar radial distance (r).
     */
    public double getR() {
        computePolar();
        return r;
    }

    /**
     * Returns a {@link Vector} that is the sum of this {@link Vector} and the given
     * {@link Vector}.
     *
     * @param vec
     *            {@link Vector} to add
     * @return Resulting {@link Vector}
     */
    public Vector add(Vector vec) {
        return new Vector(
            x + vec.x,
            y + vec.y,
            z + vec.z
        );
    }

    /**
     * Returns a {@link Vector} that is the difference of this {@link Vector} and the
     * given {@link Vector}.
     *
     * @param vec
     *            {@link Vector} to subtract
     * @return Resulting {@link Vector}
     */
    public Vector subtract(Vector vec) {
        return new Vector(
            x - vec.x,
            y - vec.y,
            z - vec.z
        );
    }

    /**
     * Returns a {@link Vector} that is the scalar product of this {@link Vector} and the
     * given scalar.
     *
     * @param scalar
     *            Scalar to multiply
     * @return Resulting {@link Vector}
     */
    public Vector multiply(double scalar) {
        return new Vector(
            x * scalar,
            y * scalar,
            z * scalar
        );
    }

    /**
     * Returns the negation of this {@link Vector}.
     *
     * @return Resulting {@link Vector}
     */
    public Vector negate() {
        return new Vector(
            -x,
            -y,
            -z
        );
    }

    /**
     * Returns a {@link Vector} that is the cross product of this {@link Vector} and the
     * given {@link Vector}.
     *
     * @param right
     *            {@link Vector} to multiply
     * @return Resulting {@link Vector}
     */
    public Vector cross(Vector right) {
        return new Vector(
            y * right.z - z * right.y,
            z * right.x - x * right.z,
            x * right.y - y * right.x
        );
    }

    /**
     * Returns the dot product of this {@link Vector} and the given {@link Vector}.
     *
     * @param right
     *            {@link Vector} to multiply
     * @return Resulting dot product
     */
    public double dot(Vector right) {
        return x * right.x + y * right.y + z * right.z;
    }

    /**
     * Returns the norm of this {@link Vector}.
     *
     * @return Norm of this vector
     */
    public double norm() {
        return sqrt(dot(this));
    }

    /**
     * Ensures that the polar coordinates are available.
     */
    private synchronized void computePolar() {
        if (hasPolar) {
            return;
        }

        double ρSqr = x * x + y * y;

        r = sqrt(ρSqr + z * z);

        if (isZero(x) && isZero(y)) {
            φ = 0.0;
        } else {
            φ = atan2(y, x);
        }

        if (φ < 0.0) {
            φ += PI2;
        }

        if (isZero(z) && isZero(ρSqr)) {
            θ = 0.0;
        } else {
            θ = atan2(z, sqrt(ρSqr));
        }

        hasPolar = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Vector)) {
            return false;
        }

        Vector vec = (Vector) obj;
        return Double.compare(x, vec.x) == 0
            && Double.compare(y, vec.y) == 0
            && Double.compare(z, vec.z) == 0;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(x).hashCode()
            ^  Double.valueOf(y).hashCode()
            ^  Double.valueOf(z).hashCode();
    }

    @Override
    public String toString() {
        return "(x=" + x + ", y=" + y + ", z=" + z + ")";
    }

}

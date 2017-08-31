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

/**
 * Calculates the roots and extremum of a quadratic equation.
 */
public class QuadraticInterpolation {

    private final double xe;
    private final double ye;
    private final double root1;
    private final double root2;
    private final int nRoot;

    /**
     * Creates a new quadratic equation.
     *
     * @param yMinus
     *            y at x == -1
     * @param y0
     *            y at x == 0
     * @param yPlus
     *            y at x == 1
     */
    public QuadraticInterpolation(double yMinus, double y0, double yPlus) {
        double a = 0.5 * (yPlus + yMinus) - y0;
        double b = 0.5 * (yPlus - yMinus);
        double c = y0;

        xe = -b / (2.0 * a);
        ye = (a * xe + b) * xe + c;
        double dis = b * b - 4.0 * a * c;

        int rootCount = 0;

        if (dis >= 0.0) {
            double dx = 0.5 * sqrt(dis) / abs(a);
            root1 = xe - dx;
            root2 = xe + dx;

            if (abs(root1) <= 1.0) {
                rootCount++;
            }

            if (abs(root2) <= 1.0) {
                rootCount++;
            }
        } else {
            root1 = Double.NaN;
            root2 = Double.NaN;
        }

        nRoot = rootCount;
    }

    /**
     * Returns X of extremum. Can be outside [-1 .. 1].
     *
     * @return X
     */
    public double getXe() {
        return xe;
    }

    /**
     * Returns the Y value at the extremum.
     *
     * @return Y
     */
    public double getYe() {
        return ye;
    }

    /**
     * Returns the first root that was found.
     *
     * @return X of first root
     */
    public double getRoot1() {
        return root1 < -1.0 ? root2 : root1;
    }

    /**
     * Returns the second root that was found.
     *
     * @return X of second root
     */
    public double getRoot2() {
        return root2;
    }

    /**
     * Returns the number of roots found in [-1 .. 1].
     *
     * @return Number of roots
     */
    public int getNumberOfRoots() {
        return nRoot;
    }

}

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

import static java.lang.Math.abs;


/**
 * Finds the root of a function by using the Pegasus method.
 *
 * @see <a href="https://en.wikipedia.org/wiki/False_position_method">regula falsi</a>
 * @since 2.3
 */

public class Pegasus {

    private static final int MAX_ITERATIONS = 30;

    /**
     * Find the root of the given function within the boundaries.
     *
     * @param lower
     *            Lower boundary
     * @param upper
     *            Upper boundary
     * @param accuracy
     *            Desired accuracy
     * @param f
     *            Function to be used for calculation
     * @return root that was found
     * @throws ArithmeticException
     *             if the root could not be found in the given accuracy within
     *             {@value #MAX_ITERATIONS} iterations.
     */
    public static Double calculate(double lower, double upper, double accuracy, Function f) {
        double x1 = lower;
        double x2 = upper;

        double f1 = f.apply(x1);
        double f2 = f.apply(x2);

        if (f1 * f2 >= 0.0) {
            throw new ArithmeticException("No root within the given boundaries");
        }

        int i = MAX_ITERATIONS;

        while (i-- > 0) {
            double x3 = x2 - f2 / ((f2 - f1) / (x2 - x1));
            double f3 = f.apply(x3);

            if (f3 * f2 <= 0.0) {
                x1 = x2;
                f1 = f2;
                x2 = x3;
                f2 = f3;
            } else {
                f1 = f1 * f2 / (f2 + f3);
                x2 = x3;
                f2 = f3;
            }

            if (abs(x2 - x1) <= accuracy) {
                return abs(f1) < abs(f2) ? x1 : x2;
            }
        }

        throw new ArithmeticException("Maximum number of iterations exceeded");
    }

    /**
     * The function that is to be solved.
     *
     * @since 2.3
     */
    public interface Function {

        /**
         * Calculate the function result for x.
         *
         * @param x
         *            x
         * @return f(x)
         */
        double apply(double x);

    }

}

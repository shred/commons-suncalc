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

import java.util.Comparator;
import java.util.function.Function;

/**
 * Contains constants and mathematical operations that are not available in {@link Math}.
 */
public final class ExtendedMath {

    /**
     * PI * 2
     */
    public static final double PI2 = PI * 2.0;

    /**
     * Arc-Seconds per Radian.
     */
    public static final double ARCS = toDegrees(3600.0);

    /**
     * Mean radius of the earth, in kilometers.
     */
    public static final double EARTH_MEAN_RADIUS = 6371.0;

    /**
     * Refraction at the horizon, in radians.
     */
    public static final double REFRACTION_AT_HORIZON = PI / (tan(toRadians(7.31 / 4.4)) * 10800.0);

    private ExtendedMath() {
        // utility class without constructor
    }

    /**
     * Returns the decimal part of a value.
     *
     * @param a
     *            Value
     * @return Fraction of that value. It has the same sign as the input value.
     */
    public static double frac(double a) {
        return a % 1.0;
    }

    /**
     * Performs a safe check if the given double is actually zero (0.0).
     * <p>
     * Note that "almost zero" returns {@code false}, so this method should not be used
     * for comparing calculation results to zero.
     *
     * @param d
     *            double to check for zero.
     * @return {@code true} if the value was zero, or negative zero.
     */
    public static boolean isZero(double d) {
        // This should keep squid:S1244 silent...
        return !Double.isNaN(d) && round(signum(d)) == 0L;
    }

    /**
     * Converts equatorial coordinates to horizontal coordinates.
     *
     * @param tau
     *            Hour angle (radians)
     * @param dec
     *            Declination (radians)
     * @param dist
     *            Distance of the object
     * @param lat
     *            Latitude of the observer (radians)
     * @return {@link Vector} containing the horizontal coordinates
     */
    public static Vector equatorialToHorizontal(double tau, double dec, double dist, double lat) {
        return Matrix.rotateY(PI / 2.0 - lat).multiply(Vector.ofPolar(tau, dec, dist));
    }

    /**
     * Creates a rotational {@link Matrix} for converting equatorial to ecliptical
     * coordinates.
     *
     * @param t
     *            {@link JulianDate} to use
     * @return {@link Matrix} for converting equatorial to ecliptical coordinates
     */
    public static Matrix equatorialToEcliptical(JulianDate t) {
        double jc = t.getJulianCentury();
        double eps = toRadians(23.43929111 - (46.8150 + (0.00059 - 0.001813 * jc) * jc) * jc / 3600.0);
        return Matrix.rotateX(eps);
    }

    /**
     * Returns the parallax for objects at the horizon.
     *
     * @param elevation
     *            Observer's elevation, in meters above sea level. Must not be negative.
     * @param distance
     *            Distance of the sun, in kilometers.
     * @return parallax, in radians
     */
    public static double parallax(double elevation, double distance) {
        return asin(EARTH_MEAN_RADIUS / distance)
             - acos(EARTH_MEAN_RADIUS / (EARTH_MEAN_RADIUS + (elevation / 1000.0)));
    }

    /**
     * Calculates the atmospheric refraction of an object at the given apparent altitude.
     * <p>
     * The result is only valid for positive altitude angles. If negative, 0.0 is
     * returned.
     * <p>
     * Assumes an atmospheric pressure of 1010 hPa and a temperature of 10 °C.
     *
     * @param ha
     *            Apparent altitude, in radians.
     * @return Refraction at this altitude
     * @see <a href="https://en.wikipedia.org/wiki/Atmospheric_refraction">Wikipedia:
     *      Atmospheric Refraction</a>
     */
    public static double apparentRefraction(double ha) {
        if (ha < 0.0) {
            return 0.0;
        }

        if (isZero(ha)) {
            return REFRACTION_AT_HORIZON;
        }

        return PI / (tan(toRadians(ha + (7.31 / (ha + 4.4)))) * 10800.0);
    }

    /**
     * Calculates the atmospheric refraction of an object at the given altitude.
     * <p>
     * The result is only valid for positive altitude angles. If negative, 0.0 is
     * returned.
     * <p>
     * Assumes an atmospheric pressure of 1010 hPa and a temperature of 10 °C.
     *
     * @param h
     *            True altitude, in radians.
     * @return Refraction at this altitude
     * @see <a href="https://en.wikipedia.org/wiki/Atmospheric_refraction">Wikipedia:
     *      Atmospheric Refraction</a>
     */
    public static double refraction(double h) {
        if (h < 0.0) {
            return 0.0;
        }

        // refraction formula, converted to radians
        return 0.000296706 / tan(h + 0.00312537 / (h + 0.0890118));
    }

    /**
     * Converts dms to double.
     *
     * @param d
     *            Degrees. Sign is used for result.
     * @param m
     *            Minutes. Sign is ignored.
     * @param s
     *            Seconds and fractions. Sign is ignored.
     * @return angle, in degrees
     */
    public static double dms(int d, int m, double s) {
        double sig = d < 0 ? -1.0 : 1.0;
        return sig * ((abs(s) / 60.0 + abs(m)) / 60.0 + abs(d));
    }

    /**
     * Locates the true maximum within the given time frame.
     *
     * @param time
     *         Base time
     * @param frame
     *         Time frame, which is added to and subtracted from the base time for the
     *         interval
     * @param depth
     *         Maximum recursion depth. For each recursion, the function is invoked once.
     * @param f
     *         Function to be used for calculation
     * @return time of the true maximum
     */
    public static double readjustMax(double time, double frame, int depth, Function<Double, Double> f) {
        double left = time - frame;
        double right = time + frame;
        double leftY = f.apply(left);
        double rightY = f.apply(right);

        return readjustInterval(left, right, leftY, rightY, depth, f, Double::compare);
    }

    /**
     * Locates the true minimum within the given time frame.
     *
     * @param time
     *         Base time
     * @param frame
     *         Time frame, which is added to and subtracted from the base time for the
     *         interval
     * @param depth
     *         Maximum recursion depth. For each recursion, the function is invoked once.
     * @param f
     *         Function to be used for calculation
     * @return time of the true minimum
     */
    public static double readjustMin(double time, double frame, int depth, Function<Double, Double> f) {
        double left = time - frame;
        double right = time + frame;
        double leftY = f.apply(left);
        double rightY = f.apply(right);

        return readjustInterval(left, right, leftY, rightY, depth, f, (yl, yr) -> Double.compare(yr, yl));
    }

    /**
     * Recursively find the true maximum/minimum within the given time frame.
     *
     * @param left
     *         Left interval border
     * @param right
     *         Right interval border
     * @param yl
     *         Function result at the left interval
     * @param yr
     *         Function result at the right interval
     * @param depth
     *         Maximum recursion depth. For each recursion, the function is invoked once.
     * @param f
     *         Function to invoke
     * @param cmp
     *         Comparator to decide whether the left or right side of the interval half is
     *         to be used
     * @return Position of the approximated minimum/maximum
     */
    private static double readjustInterval(double left, double right, double yl, double yr, int depth,
                                           Function<Double, Double> f, Comparator<Double> cmp) {
        if (depth <= 0) {
            return (cmp.compare(yl, yr) < 0) ? right : left;
        }

        double middle = (left + right) / 2.0;
        double ym = f.apply(middle);
        if (cmp.compare(yl, yr) < 0) {
            return readjustInterval(middle, right, ym, yr, depth - 1, f, cmp);
        } else {
            return readjustInterval(left, middle, yl, ym, depth - 1, f, cmp);
        }
    }

}

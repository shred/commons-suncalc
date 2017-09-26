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
     * Apparent refraction at the horizon, in radians.
     */
    public static final double APPARENT_REFRACTION = PI / (tan(toRadians(7.31 / 4.4)) * 10800.0);

    /**
     * Mean radius of the earth, in kilometers.
     */
    public static final double EARTH_MEAN_RADIUS = 6371.0;

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
     * Calculates the atmospheric refraction of an object at the given altitude.
     * <p>
     * The result is only valid for positive altitude angles. If negative, 0.0 is
     * returned.
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

}

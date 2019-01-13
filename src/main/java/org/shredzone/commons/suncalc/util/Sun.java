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
 * Calculations and constants for the Sun.
 *
 * @see "Astronomy on the Personal Computer, 4th edition
 *      (Oliver Montenbruck, Thomas Pfleger) -
 *      ISBN 978-3-540-67221-0"
 */

public final class Sun {

    private static final double SUN_DISTANCE = 149598000.0;
    private static final double SUN_MEAN_RADIUS = 695700.0;

    private Sun() {
        // Utility class without constructor
    }

    /**
     * Calculates the equatorial position of the sun.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @return {@link Vector} containing the sun position
     */
    public static Vector positionEquatorial(JulianDate date) {
        double T = date.getJulianCentury();
        double M = PI2 * frac(0.993133 + 99.997361 * T);
        double L = PI2 * frac(0.7859453 + M / PI2
            + (6893.0 * sin(M) + 72.0 * sin(2.0 * M) + 6191.2 * T) / 1296.0e3);

        double d = SUN_DISTANCE
            * (1 - 0.016718 * cos(date.getTrueAnomaly()));

        return Vector.ofPolar(L, 0.0, d);
    }

    /**
     * Calculates the geocentric position of the sun.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @return {@link Vector} containing the sun position
     */
    public static Vector position(JulianDate date) {
        Matrix rotateMatrix = equatorialToEcliptical(date).transpose();
        return rotateMatrix.multiply(positionEquatorial(date));
    }

    /**
     * Calculates the horizontal position of the sun.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @param lat
     *            Latitude, in radians
     * @param lng
     *            Longitute, in radians
     * @return {@link Vector} of horizontal sun position
     */
    public static Vector positionHorizontal(JulianDate date, double lat, double lng) {
        Vector mc = position(date);
        double h = date.getGreenwichMeanSiderealTime() + lng - mc.getPhi();
        return equatorialToHorizontal(h, mc.getTheta(), mc.getR(), lat);
    }

    /**
     * Returns the angular radius of the sun.
     *
     * @param distance
     *            Distance of the sun, in kilometers.
     * @return Angular radius of the sun, in radians.
     * @see <a href="https://en.wikipedia.org/wiki/Angular_diameter">Wikipedia: Angular
     *      Diameter</a>
     */
    public static double angularRadius(double distance) {
        return asin(SUN_MEAN_RADIUS / distance);
    }

}

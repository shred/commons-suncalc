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
 * Calculations and constants for the Moon.
 *
 * @see "Astronomy on the Personal Computer, 4th edition
 *      (Oliver Montenbruck, Thomas Pfleger) -
 *      ISBN 978-3-540-67221-0"
 */
public final class Moon {

    private static final double MOON_MEAN_RADIUS = 1737.1;

    private Moon() {
        // Utility class without constructor
    }

    /**
     * Calculates the equatorial position of the moon.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @return {@link Vector} of equatorial moon position
     */
    public static Vector positionEquatorial(JulianDate date) {
        double T  = date.getJulianCentury();
        double L0 =       frac(0.606433 + 1336.855225 * T);
        double l  = PI2 * frac(0.374897 + 1325.552410 * T);
        double ls = PI2 * frac(0.993133 +   99.997361 * T);
        double D  = PI2 * frac(0.827361 + 1236.853086 * T);
        double F  = PI2 * frac(0.259086 + 1342.227825 * T);
        double D2 = 2.0 * D;
        double l2 = 2.0 * l;
        double F2 = 2.0 * F;

        double dL = 22640.0 * sin(l)
                  -  4586.0 * sin(l - D2)
                  +  2370.0 * sin(D2)
                  +   769.0 * sin(l2)
                  -   668.0 * sin(ls)
                  -   412.0 * sin(F2)
                  -   212.0 * sin(l2 - D2)
                  -   206.0 * sin(l + ls - D2)
                  +   192.0 * sin(l + D2)
                  -   165.0 * sin(ls - D2)
                  -   125.0 * sin(D)
                  -   110.0 * sin(l + ls)
                  +   148.0 * sin(l - ls)
                  -    55.0 * sin(F2 - D2);

        double S  = F + (dL + 412.0 * sin(F2) + 541.0 * sin(ls)) / ARCS;
        double h  = F - D2;
        double N  =  -526.0 * sin(h)
                  +    44.0 * sin(l + h)
                  -    31.0 * sin(-l + h)
                  -    23.0 * sin(ls + h)
                  +    11.0 * sin(-ls + h)
                  -    25.0 * sin(-l2 + F)
                  +    21.0 * sin(-l + F);

        double l_Moon = PI2 * frac(L0 + dL / 1296.0e3);
        double b_Moon = (18520.0 * sin(S) + N) / ARCS;

        double dt = 385000.5584
                  -  20905.3550 * cos(l)
                  -   3699.1109 * cos(D2 - l)
                  -   2955.9676 * cos(D2)
                  -    569.9251 * cos(l2);

        return Vector.ofPolar(l_Moon, b_Moon, dt);
    }

    /**
     * Calculates the geocentric position of the moon.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @return {@link Vector} of geocentric moon position
     */
    public static Vector position(JulianDate date) {
        Matrix rotateMatrix = equatorialToEcliptical(date).transpose();
        return rotateMatrix.multiply(positionEquatorial(date));
    }

    /**
     * Calculates the horizontal position of the moon.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @param lat
     *            Latitude, in radians
     * @param lng
     *            Longitute, in radians
     * @return {@link Vector} of horizontal moon position
     */
    public static Vector positionHorizontal(JulianDate date, double lat, double lng) {
        Vector mc = position(date);
        double h = date.getGreenwichMeanSiderealTime() + lng - mc.getPhi();
        return equatorialToHorizontal(h, mc.getTheta(), mc.getR(), lat);
    }

    /**
     * Calculates the topocentric position of the moon.
     * <p>
     * Atmospheric refraction is <em>not</em> taken into account.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @param lat
     *            Latitude, in radians
     * @param lng
     *            Longitute, in radians
     * @param elev
     *            Elevation, in meters
     * @return {@link Vector} of topocentric moon position
     * @since 3.9
     */
    public static Vector positionTopocentric(JulianDate date, double lat, double lng, double elev) {
        Vector pos = positionHorizontal(date, lat, lng);
        return Vector.ofPolar(
                pos.getPhi(),
                pos.getTheta() - parallax(elev, pos.getR()),
                pos.getR()
        );
    }

    /**
     * Returns the angular radius of the moon.
     *
     * @param distance
     *            Distance of the moon, in kilometers.
     * @return Angular radius of the moon, in radians.
     * @see <a href="https://en.wikipedia.org/wiki/Angular_diameter">Wikipedia: Angular
     *      Diameter</a>
     */
    public static double angularRadius(double distance) {
        return asin(MOON_MEAN_RADIUS / distance);
    }

}

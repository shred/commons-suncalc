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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Calculations and constants for the Moon.
 *
 * @see "Astronomy on the Personal Computer, 4th edition
 *      (Oliver Montenbruck, Thomas Pfleger) -
 *      ISBN 978-3-540-67221-0"
 */
@ParametersAreNonnullByDefault
public final class Moon {

    private static final double MOON_MEAN_RADIUS = 1737.1;

    private Moon() {
        // Utility class without constructor
    }

    /**
     * Calculates the geocentric position of the moon.
     *
     * @param date
     *            {@link JulianDate} to be used
     * @return {@link Vector} of geocentric moon position
     */
    public static Vector position(JulianDate date) {
        double T  = date.getJulianCentury();
        double L0 =       frac(0.606433 + 1336.855225 * T);
        double l  = PI2 * frac(0.374897 + 1325.552410 * T);
        double ls = PI2 * frac(0.993133 +   99.997361 * T);
        double D  = PI2 * frac(0.827361 + 1236.853086 * T);
        double F  = PI2 * frac(0.259086 + 1342.227825 * T);

        double dL = 22640 * sin(l)
                  -  4586 * sin(l - 2 * D)
                  +  2370 * sin(2 * D)
                  +   769 * sin(2 * l)
                  -   668 * sin(ls)
                  -   412 * sin(2 * F)
                  -   212 * sin(2 * l - 2 * D)
                  -   206 * sin(l + ls - 2 * D)
                  +   192 * sin(l + 2 * D)
                  -   165 * sin(ls - 2 * D)
                  -   125 * sin(D)
                  -   110 * sin(l + ls)
                  +   148 * sin(l - ls)
                  -    55 * sin(2 * F - 2 * D);

        double S  = F + (dL + 412 * sin(2 * F) + 541 * sin(ls)) / ARCS;
        double h  = F - 2 * D;
        double N  =  -526 * sin(h)
                  +    44 * sin(l + h)
                  -    31 * sin(-l + h)
                  -    23 * sin(ls + h)
                  +    11 * sin(-ls + h)
                  -    25 * sin(-2 * l + F)
                  +    21 * sin(-l + F);

        double l_Moon = PI2 * frac(L0 + dL / 1296.0e3);
        double b_Moon = (18520.0 * sin(S) + N) / ARCS;

        double dt = 385000.6 - 20905.0 * cos(l);

        Matrix rotateMatrix = equatorialToEcliptical(date).transpose();
        return rotateMatrix.multiply(Vector.ofPolar(l_Moon, b_Moon, dt));
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

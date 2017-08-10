/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2016 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Bases on SunCalc by Vladimir Agafonkin (https://github.com/mourner/suncalc)
 */
package org.shredzone.commons.suncalc;

import static java.lang.Math.*;
import static org.shredzone.commons.suncalc.util.Kopernikus.*;

import java.util.Date;

import org.shredzone.commons.suncalc.util.Kopernikus.Coordinates;

/**
 * Calculates the position of the moon.
 *
 * @see <a href="https://github.com/mourner/suncalc">SunCalc</a>
 * @see <a href="http://aa.quae.nl/en/reken/hemelpositie.html">Formulas used for moon
 *      calculations</a>
 * @see "Astronomical Algorithms 2nd edition by Jean Meeus (Willmann-Bell, Richmond) 1998"
 */
public class MoonPosition {

    private final double azimuth;
    private final double altitude;
    private final double distance;
    private final double parallacticAngle;

    private MoonPosition(double azimuth, double altitude, double distance, double parallacticAngle) {
        this.azimuth = azimuth;
        this.altitude = altitude;
        this.distance = distance;
        this.parallacticAngle = parallacticAngle;
    }


    /**
     * Calculates the {@link MoonPosition} of the given date and location.
     *
     * @param date
     *            {@link Date} to compute the moon position of
     * @param lat
     *            Latitude
     * @param lng
     *            Longitude
     * @return Calculated {@link MoonPosition}
     */
    public static MoonPosition of(Date date, double lat, double lng) {
        double lw  = RAD * -lng;
        double phi = RAD * lat;
        double d   = toDays(date);

        Coordinates c = moonCoords(d);
        double sH = siderealTime(d, lw) - c.ra;
        double h = altitude(sH, phi, c.dec);
        // formula 14.1 of "Astronomical Algorithms" 2nd edition by Jean Meeus (Willmann-Bell, Richmond) 1998.
        double pa = atan2(sin(sH), tan(phi) * cos(c.dec)) - sin(c.dec) * cos(sH);

        h += astroRefraction(h); // altitude correction for refraction

        return new MoonPosition(azimuth(sH, phi, c.dec), h, c.dist, pa);
    }

    /**
     * Moon azimuth in radians.
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * Moon altitude above the horizon in radians.
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Distance to moon in kilometers.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Parallactic angle of the moon in radians.
     */
    public double getParallacticAngle() {
        return parallacticAngle;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonPosition[azimuth=").append(azimuth);
        sb.append(", altitude=").append(altitude);
        sb.append(", distance=").append(distance);
        sb.append(", parallacticAngle=").append(parallacticAngle);
        sb.append(']');
        return sb.toString();
    }

    private static double astroRefraction(double h) {
        // the following formula works for positive altitudes only.
        // if h = -0.08901179 a div/0 would occur.
        double hPos = Math.max(h, 0.0);

        // formula 16.4 of "Astronomical Algorithms" 2nd edition by Jean Meeus (Willmann-Bell, Richmond) 1998.
        // 1.02 / tan(h + 10.26 / (h + 5.10)) h in degrees, result in arc minutes -> converted to rad:
        return 0.0002967 / tan(hPos + 0.00312536 / (hPos + 0.08901179));
    }

}

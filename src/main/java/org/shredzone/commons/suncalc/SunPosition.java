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

import static org.shredzone.commons.suncalc.util.Kopernikus.*;

import java.util.Date;

import org.shredzone.commons.suncalc.util.Kopernikus.Coordinates;

/**
 * Calculates the position of the sun.
 *
 * @see <a href="https://github.com/mourner/suncalc">SunCalc</a>
 * @see <a href="http://aa.quae.nl/en/reken/zonpositie.html">Formulas used for sun
 *      calculations</a>
 */
public class SunPosition {

    private final double azimuth;
    private final double altitude;

    private SunPosition(double azimuth, double altitude) {
        this.azimuth = azimuth;
        this.altitude = altitude;
    }

    /**
     * Calculates the {@link SunPosition} of the given {@link Date} and location.
     *
     * @param date
     *            {@link Date} to compute the sun position of
     * @param lat
     *            Latitude
     * @param lng
     *            Longitude
     * @return Calculated {@link SunPosition}
     */
    public static SunPosition of(Date date, double lat, double lng) {
        double lw = RAD * -lng;
        double phi = RAD * lat;
        double d = toDays(date);
        Coordinates c = sunCoords(d);
        double h = siderealTime(d, lw) - c.ra;

        return new SunPosition(azimuth(h, phi, c.dec), altitude(h, phi, c.dec));
    }

    /**
     * Sun altitude above the horizon in radians.
     * <p>
     * {@code 0} means the sun is at the horizon, {@code PI / 2} at the zenith (straight
     * over your head).
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Sun azimuth in radians.
     * <p>
     * This is the direction along the horizon, measured from south to west. For example,
     * {@code 0} means south, {@code PI * 3 / 4} means northwest.
     */
    public double getAzimuth() {
        return azimuth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunPosition[azimuth=").append(azimuth);
        sb.append(", altitude=").append(altitude);
        sb.append(']');
        return sb.toString();
    }

}

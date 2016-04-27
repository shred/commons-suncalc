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
 * @author Richard "Shred" Körber
 */
public class SunPosition {

    /**
     * Calculates the {@link SunPosition} of the given {@link Date} and location.
     */
    public static SunPosition of(Date date, double lat, double lng) {
        double lw = RAD * -lng;
        double phi = RAD * lat;
        double d = toDays(date);
        Coordinates c = sunCoords(d);
        double h = siderealTime(d, lw) - c.ra;

        return new SunPosition(azimuth(h, phi, c.dec), altitude(h, phi, c.dec));
    };

    private final double azimuth, altitude;

    private SunPosition(double azimuth, double altitude) {
        this.azimuth = azimuth;
        this.altitude = altitude;
    }

    public double getAltitude() {
        return altitude;
    }

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

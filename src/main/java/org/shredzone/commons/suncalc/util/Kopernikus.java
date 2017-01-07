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
package org.shredzone.commons.suncalc.util;

import static java.lang.Math.*;

import java.util.Date;
import java.util.TimeZone;

/**
 * An internal utility class with common constants and functions.
 *
 * @author Richard "Shred" Körber
 */
public final class Kopernikus {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final double RAD = PI / 180.0;
    public static final long DAY_MS = 1000L * 60 * 60 * 24;
    public static final long J1970 = 2440588L;
    public static final long J2000 = 2451545L;
    public static final double E = RAD * 23.4397; // obliquity of the Earth

    private Kopernikus() {
        // Utility class without constructor
    }

    public static double toDays(Date date) {
        double julian = ((double) date.getTime()) / DAY_MS - 0.5 + J1970;
        return julian - J2000;
    }

    public static Coordinates moonCoords(double d) {
        double l = RAD * (218.316 + 13.176396 * d); // ecliptic longitude
        double m = RAD * (134.963 + 13.064993 * d); // mean anomaly
        double f = RAD * (93.272 + 13.229350 * d);  // mean distance

        double lon = l + RAD * 6.289 * sin(m);    // longitude
        double b = RAD * 5.128 * sin(f);          // latitude
        double dt = 385001.0 - 20905.0 * cos(m);  // distance to the moon in km

        return new Coordinates(declination(lon, b), rightAscension(lon, b), dt);
    }

    public static Coordinates sunCoords(double d) {
        double m = solarMeanAnomaly(d);
        double l = eclipticLongitude(m);

        return new Coordinates(declination(l, 0.0), rightAscension(l, 0.0));
    }

    public static double solarMeanAnomaly(double d) {
        return RAD * (357.5291 + 0.98560028 * d);
    }

    public static double eclipticLongitude(double m) {
        double c = RAD * (1.9148 * sin(m) + 0.02 * sin(2.0 * m) + 0.0003 * sin(3 * m)); // equation of center
        double p = RAD * 102.9372; // perihelion of the Earth

        return m + c + p + PI;
    }

    public static double azimuth(double h, double phi, double dec) {
        return atan2(sin(h), cos(h) * sin(phi) - tan(dec) * cos(phi));
    }

    public static double altitude(double h, double phi, double dec) {
        return asin(sin(phi) * sin(dec) + cos(phi) * cos(dec) * cos(h));
    }

    public static double siderealTime(double d, double lw) {
        return RAD * (280.16 + 360.9856235 * d) - lw;
    }

    public static double declination(double l, double b) {
        return asin(sin(b) * cos(E) + cos(b) * sin(E) * sin(l));
    }

    public static double rightAscension(double l, double b) {
        return atan2(sin(l) * cos(E) - tan(b) * sin(E), cos(l));
    }

    public static class Coordinates {
        public final double dec;
        public final double ra;
        public final double dist;

        public Coordinates(double dec, double ra) {
            this(dec, ra, Double.NaN);
        }

        public Coordinates(double dec, double ra, double dist) {
            this.dec = dec;
            this.ra = ra;
            this.dist = dist;
        }
    }

}

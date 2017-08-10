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

/**
 * Calculates the rise and set times of the sun.
 * <p>
 * In contrast to the <a href="https://github.com/mourner/suncalc">SunCalc</a> library,
 * this class does not return a set of sunrise/sunset times. Instead, there is a fixed
 * enumeration of available sunrise/sunset times, and {@link #getTime(Time)} calculates
 * the respective time. This way, unused times are not calculated, and the time names
 * are compiler checked.
 * <p>
 * To calculate individual sunrise/sunset times, use {@link #sunriseTime(double)} and
 * {@link #sunsetTime(double)}.
 *
 * @see <a href="https://github.com/mourner/suncalc">SunCalc</a>
 * @see <a href="http://aa.quae.nl/en/reken/zonpositie.html">Formulas used for sun
 *      calculations</a>
 */
public class SunTimes {

    /**
     * Enumeration of all available sunrise/sunset times.
     */
    public enum Time {

        /**
         * sunrise (top edge of the sun appears on the horizon)
         */
        SUNRISE("sunrise", -0.833, true),

        /**
         * sunrise ends (bottom edge of the sun touches the horizon)
         */
        SUNRISE_END("sunriseEnd", -0.3, true),

        /**
         * morning golden hour (soft light, best time for photography) ends
         */
        GOLDEN_HOUR_END("goldenHourEnd", 6.0, true),

        /**
         * solar noon (sun is in the highest position)
         */
        SOLAR_NOON("solarNoon", null, true),

        /**
         * evening golden hour starts
         */
        GOLDEN_HOUR("goldenHour", 6.0, false),

        /**
         * sunset starts (bottom edge of the sun touches the horizon)
         */
        SUNSET_START("sunsetStart", -0.3, false),

        /**
         * sunset (sun disappears below the horizon, evening civil twilight starts)
         */
        SUNSET("sunset", -0.833, false),

        /**
         * dusk (evening nautical twilight starts)
         */
        DUSK("dusk", -6.0, false),

        /**
         * nautical dusk (evening astronomical twilight starts)
         */
        NAUTICAL_DUSK("nauticalDusk", -12.0, false),

        /**
         * night starts (dark enough for astronomical observations)
         */
        NIGHT("night", -18.0, false),

        /**
         * nadir (darkest moment of the night, sun is in the lowest position)
         */
        NADIR("nadir", null, false),

        /**
         * night ends (morning astronomical twilight starts)
         */
        NIGHT_END("nightEnd", -18.0, true),

        /**
         * nautical dawn (morning nautical twilight starts)
         */
        NAUTICAL_DAWN("nauticalDawn", -12.0, true),

        /**
         * dawn (morning nautical twilight ends, morning civil twilight starts)
         */
        DAWN("dawn", -6.0, true),
        ;

        private final String key;
        private final Double angle;
        private final boolean rising;

        private Time(String key, Double angle, boolean rising) {
            this.key = key;
            this.angle = angle;
            this.rising = rising;
        }

        /**
         * Parses the property name as defined in the
         * <a href="https://github.com/mourner/suncalc#sunlight-times">SunCalc JavaScript
         * library</a>.
         *
         * @param name
         *            Name to parse
         * @return {@link Time}, or {@code null} if the name is not known.
         */
        public static Time parse(String name) {
            for (Time t : values()) {
                if (t.toString().equals(name)) {
                    return t;
                }
            }
            return null;
        }

        /**
         * Returns the sun's angle. {@code null} for solar noon, nadir.
         */
        public Double getAngle() {
            return angle;
        }

        /**
         * Returns {@code true} if the sun is rising, {@code false} if it is setting.
         */
        public boolean isRising() {
            return rising;
        }

        /**
         * Returns the property name as defined in the
         * <a href="https://github.com/mourner/suncalc#sunlight-times">SunCalc JavaScript
         * library</a>.
         */
        @Override
        public String toString() {
            return key;
        }
    }

    private static final double J0 = 0.0009;

    private final double jnoon;
    private final double lw;
    private final double phi;
    private final double dec;
    private final double m;
    private final double l;
    private final long n;

    private SunTimes(double jnoon, double lw, double phi, double dec, long n, double m, double l) {
        this.jnoon = jnoon;
        this.lw = lw;
        this.phi = phi;
        this.dec = dec;
        this.n = n;
        this.m = m;
        this.l = l;
    }

    /**
     * Calculates the {@link SunTimes} of the given {@link Date} and location.
     *
     * @param date
     *            {@link Date} to compute the sun times of
     * @param lat
     *            Latitude
     * @param lng
     *            Longitude
     * @return Calculated {@link SunTimes}
     */
    public static SunTimes of(Date date, double lat, double lng) {
        double lw = RAD * -lng;
        double phi = RAD * lat;

        double d = toDays(date);
        long n = julianCycle(d, lw);
        double ds = approxTransit(0, lw, n);

        double m = solarMeanAnomaly(ds);
        double l = eclipticLongitude(m);
        double dec = declination(l, 0);

        double jnoon = solarTransitJ(ds, m, l);

        return new SunTimes(jnoon, lw, phi, dec, n, m, l);
    }

    /**
     * Returns the time of the given type.
     *
     * @param time
     *            Time type
     * @return Time, or {@code null} if the sun does not reach the {@link Time} on the
     *         given date (e.g. midnight sun).
     */
    public Date getTime(Time time) {
        if (time == Time.SOLAR_NOON) {
            return fromJulian(jnoon);
        } else if (time == Time.NADIR) {
            return fromJulian(jnoon - 0.5);
        } else if (time.isRising()){
            return sunriseTime(time.getAngle());
        } else {
            return sunsetTime(time.getAngle());
        }
    }

    /**
     * Calculates the time when the rising sun reaches the given angle.
     *
     * @param angle
     *            Sun's angle
     * @return Time, or {@code null} if the sun does not reach the angle on the
     *         given date (e.g. midnight sun).
     */
    public Date sunriseTime(double angle) {
        double jset = getSetJ(angle * RAD, lw, phi, dec, n, m, l);
        double jrise = jnoon - (jset - jnoon);
        return fromJulian(jrise);
    }

    /**
     * Calculates the time when the setting sun reaches the given angle.
     *
     * @param angle
     *            Sun's angle
     * @return Time, or {@code null} if the sun does not reach the angle on the given date
     *         (e.g. midnight sun).
     */
    public Date sunsetTime(double angle) {
        double jset = getSetJ(angle * RAD, lw, phi, dec, n, m, l);
        return fromJulian(jset);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunTimes[");
        Time[] times = Time.values();
        for (int ix = 0; ix < times.length; ix++) {
            if (ix > 0) {
                sb.append(", ");
            }
            sb.append(times[ix]).append('=').append(getTime(times[ix]));
        }
        sb.append(']');
        return sb.toString();
    }

    private static long julianCycle(double d, double lw) {
        return round(d - J0 - lw / (2 * PI));
    }

    private static Date fromJulian(double j) {
        if (Double.isNaN(j)) {
            return null;
        }
        return new Date(round((j + 0.5 - J1970) * DAY_MS));
    }

    private static double getSetJ(double h, double lw, double phi, double dec, long n, double m, double l) {
        double w = hourAngle(h, phi, dec);
        double a = approxTransit(w, lw, n);
        return solarTransitJ(a, m, l);
    }

    private static double hourAngle(double h, double phi, double d) {
        return acos((sin(h) - sin(phi) * sin(d)) / (cos(phi) * cos(d)));
    }

    private static double approxTransit(double ht, double lw, long n) {
        return J0 + (ht + lw) / (2 * PI) + n;
    }

    private static double solarTransitJ(double ds, double m, double l) {
        return J2000 + ds + 0.0053 * sin(m) - 0.0069 * sin(2 * l);
    }

}

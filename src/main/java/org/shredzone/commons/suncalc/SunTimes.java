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
import java.util.Map;
import java.util.TreeMap;

/**
 * Calculates the times of the sun.
 * <p>
 * Uses a default set of times. Use {@link #addTime(double, String, String)} to add
 * further times.
 * <p>
 * Default times are:
 * <dl>
 * <dt>{@code sunrise}</dt>
 * <dd>sunrise (top edge of the sun appears on the horizon)</dd>
 * <dt>{@code sunriseEnd}</dt>
 * <dd>sunrise ends (bottom edge of the sun touches the horizon)</dd>
 * <dt>{@code goldenHourEnd}</dt>
 * <dd>morning golden hour (soft light, best time for photography) ends</dd>
 * <dt>{@code solarNoon}</dt>
 * <dd>solar noon (sun is in the highest position)</dd>
 * <dt>{@code goldenHour}</dt>
 * <dd>evening golden hour starts</dd>
 * <dt>{@code sunsetStart}</dt>
 * <dd>sunset starts (bottom edge of the sun touches the horizon)</dd>
 * <dt>{@code sunset}</dt>
 * <dd>sunset (sun disappears below the horizon, evening civil twilight starts)</dd>
 * <dt>{@code dusk}</dt>
 * <dd>dusk (evening nautical twilight starts)</dd>
 * <dt>{@code nauticalDusk}</dt>
 * <dd>nautical dusk (evening astronomical twilight starts)</dd>
 * <dt>{@code night}</dt>
 * <dd>night starts (dark enough for astronomical observations)</dd>
 * <dt>{@code nadir}</dt>
 * <dd>nadir (darkest moment of the night, sun is in the lowest position)</dd>
 * <dt>{@code nightEnd}</dt>
 * <dd>night ends (morning astronomical twilight starts)</dd>
 * <dt>{@code nauticalDawn}</dt>
 * <dd>nautical dawn (morning nautical twilight starts)</dd>
 * <dt>{@code dawn}</dt>
 * <dd>dawn (morning nautical twilight ends, morning civil twilight starts)</dd>
 * </dl>
 *
 * @see <a href="https://github.com/mourner/suncalc">SunCalc</a>
 * @see <a href="http://aa.quae.nl/en/reken/zonpositie.html">Formulas used for sun
 *      calculations</a>
 * @author Richard "Shred" Körber
 */
public class SunTimes {

    private static final double J0 = 0.0009;

    /**
     * Calculates the {@link SunTimes} of the given {@link Date} and location.
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

    private final double jnoon, lw, phi, dec, m, l;
    private final long n;
    private final Map<String, Date> times = new TreeMap<>();

    private SunTimes(double jnoon, double lw, double phi, double dec, long n, double m, double l) {
        this.jnoon = jnoon;
        this.lw = lw;
        this.phi = phi;
        this.dec = dec;
        this.n = n;
        this.m = m;
        this.l = l;

        times.put("solarNoon", fromJulian(jnoon));
        times.put("nadir", fromJulian(jnoon - 0.5));

        addTime( -0.833, "sunrise"      , "sunset");
        addTime( -0.3  , "sunriseEnd"   , "sunsetStart");
        addTime( -6.0  , "dawn"         , "dusk");
        addTime(-12.0  , "nauticalDawn" , "nauticalDusk");
        addTime(-18.0  , "nightEnd"     , "night");
        addTime(  6.0  , "goldenHourEnd", "goldenHour");
    }

    /**
     * Adds a time to the times table.
     *
     * @param angle
     *            Angle of the sun
     * @param riseName
     *            Name of the time when sun rises
     * @param setName
     *            Name of the time when sun sets
     */
    public void addTime(double angle, String riseName, String setName) {
        double jset = getSetJ(angle * RAD, lw, phi, dec, n, m, l);
        double jrise = jnoon - (jset - jnoon);

        times.put(riseName, fromJulian(jrise));
        times.put(setName, fromJulian(jset));
    }

    /**
     * Returns all times using the current times table.
     *
     * @return Map of time name and calculated date and time
     */
    public Map<String, Date> getTimes() {
        Map<String, Date> result = new TreeMap<>();
        for (Map.Entry<String, Date> entry : times.entrySet()) {
            result.put(entry.getKey(), new Date(entry.getValue().getTime()));
        }
        return result;
    }

    /**
     * Returns the time of the given type.
     *
     * @param time
     *            Time type
     * @return Time, or {@code null} if the type is not defined
     */
    public Date getTime(String time) {
        Date result = times.get(time);
        return (result != null ? new Date(result.getTime()) : null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SunTimes[");
        boolean more = false;
        for (Map.Entry<String, Date> entry : getTimes().entrySet()) {
            if (more) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue());
            more = true;
        }
        sb.append(']');
        return sb.toString();
    }

    private static long julianCycle(double d, double lw) {
        return round(d - J0 - lw / (2 * PI));
    }

    private static Date fromJulian(double j) {
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

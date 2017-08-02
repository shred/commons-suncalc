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

import org.shredzone.commons.suncalc.util.MoonCalculationsUtil;

import static java.lang.Math.*;
import static org.shredzone.commons.suncalc.util.Kopernikus.*;
import static org.shredzone.commons.suncalc.util.TimeUtil.doubleToDate;

import java.util.Date;
import java.util.TimeZone;

/**
 * Calculates the times of the moon.
 *
 * @see <a href="https://github.com/mourner/suncalc">SunCalc</a>
 * @see <a href="http://www.stargazing.net/kepler/moonrise.html">Formulas the calculation
 *      base on</a>
 * @see <a href="http://aa.quae.nl/en/reken/hemelpositie.html">Formulas used for moon
 *      calculations</a>
 * @author Richard "Shred" Körber
 */
public final class MoonTimes {

    private final Date rise;
    private final Date set;
    private final double ye;

    private MoonTimes(Date rise, Date set, double ye) {
        this.rise = rise;
        this.set = set;
        this.ye = ye;
    }

    /**
     * Calculates the {@link MoonTimes}, based on UTC.
     *
     * @param date
     *            {@link Date} to compute the moon times of
     * @param lat
     *            Latitude
     * @param lng
     *            Longitude
     * @return Calculated {@link MoonTimes}
     */
    public static MoonTimes ofUTC(Date date, double lat, double lng) {
        return of(date, lat, lng, UTC);
    }

    /**
     * Calculates the {@link MoonTimes}, based on the system's time zone.
     *
     * @param date
     *            {@link Date} to compute the moon times of
     * @param lat
     *            Latitude
     * @param lng
     *            Longitude
     * @return Calculated {@link MoonTimes}
     */
    public static MoonTimes of(Date date, double lat, double lng) {
        return of(date, lat, lng, TimeZone.getDefault());
    }

    /**
     * Calculates the {@link MoonTimes}, based on the given {@link TimeZone}.
     *
     * @param date
     *            {@link Date} to compute the moon times of
     * @param lat
     *            Latitude
     * @param lng
     *            Longitude
     * @param tz
     *            {@link TimeZone} to use
     * @return Calculated {@link MoonTimes}
     */
    public static MoonTimes of(Date date, double lat, double lng, TimeZone tz) {
        double hc = 0.133 * RAD;
        double h0 = MoonCalculationsUtil.preciseAltitude(date, tz, 0, lat, lng) - hc;
        Double rise = null;
        Double set = null;
        double ye = 0.0;

        // go in 2-hour chunks, each time seeing if a 3-point quadratic curve crosses zero (which means rise or set)
        for (int i = 1; i <= 24; i += 2) {
            double h1 = MoonCalculationsUtil.preciseAltitude(date, tz, i, lat, lng) - hc;
            double h2 = MoonCalculationsUtil.preciseAltitude(date, tz, i + 1, lat, lng) - hc;

            double a = (h0 + h2) / 2 - h1;
            double b = (h2 - h0) / 2;
            double xe = -b / (2 * a);
            ye = (a * xe + b) * xe + h1;
            double d = b * b - 4 * a * h1;
            int roots = 0;

            double x1 = 0.0;
            double x2 = 0.0;
            if (d >= 0) {
                double dx = sqrt(d) / (abs(a) * 2);
                x1 = xe - dx;
                x2 = xe + dx;
                if (abs(x1) <= 1) {
                    roots++;
                }
                if (abs(x2) <= 1) {
                    roots++;
                }
                if (x1 < -1) {
                    x1 = x2;
                }
            }

            if (roots == 1) {
                if (h0 < 0) {
                    rise = i + x1;
                } else {
                    set = i + x1;
                }
            } else if (roots == 2) {
                rise = i + (ye < 0 ? x2 : x1);
                set = i + (ye < 0 ? x1 : x2);
            }

            if (rise != null && set != null) {
                break;
            }

            h0 = h2;
        }

        return new MoonTimes(
                doubleToDate(rise, date, tz),
                doubleToDate(set, date, tz),
                ye);
    }

    /**
     * Moonrise time. {@code null} if the moon does not rise that day.
     */
    public Date getRise() {
        return rise != null ? new Date(rise.getTime()) : null;
    }

    /**
     * Moonset time. {@code null} if the moon does not set that day.
     */
    public Date getSet() {
        return set != null ? new Date(set.getTime()) : null;
    }

    /**
     * {@code true} if the moon never rises/sets, but is always above the horizon that
     * day.
     */
    public boolean isAlwaysUp() {
        return rise == null && set == null && ye > 0;
    }

    /**
     * {@code true} if the moon never rises/sets, but is always below the horizon that
     * day.
     */
    public boolean isAlwaysDown() {
        return rise == null && set == null && ye <= 0;
    }

    private static Date hoursLater(Date date, Double h) {
        if (h == null) {
            return null;
        }
        return new Date(date.getTime() + round(h * DAY_MS / 24.0));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MoonTimes[rise=").append(rise);
        sb.append(", set=").append(set);
        sb.append(", alwaysUp=").append(isAlwaysUp());
        sb.append(", alwaysDown=").append(isAlwaysDown());
        sb.append(']');
        return sb.toString();
    }

}

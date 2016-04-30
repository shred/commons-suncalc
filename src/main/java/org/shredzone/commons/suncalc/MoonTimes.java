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

import java.util.Calendar;
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

    /**
     * Calculates the {@link MoonTimes}, based on UTC.
     */
    public static MoonTimes ofUTC(Date date, double lat, double lng) {
        return of(date, lat, lng, UTC);
    }

    /**
     * Calculates the {@link MoonTimes}, based on the system's time zone.
     */
    public static MoonTimes of(Date date, double lat, double lng) {
        return of(date, lat, lng, TimeZone.getDefault());
    }

    /**
     * Calculates the {@link MoonTimes}, based on the given {@link TimeZone}.
     */
    public static MoonTimes of(Date date, double lat, double lng, TimeZone tz) {
        Calendar cal = Calendar.getInstance(tz);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date t = cal.getTime();

        double hc = 0.133 * RAD;
        double h0 = MoonPosition.of(t, lat, lng).getAltitude() - hc;
        Double rise = null, set = null;
        double ye = 0.0;

        // go in 2-hour chunks, each time seeing if a 3-point quadratic curve crosses zero (which means rise or set)
        for (int i = 1; i <= 24; i += 2) {
            double h1 = MoonPosition.of(hoursLater(t, (double) i), lat, lng).getAltitude() - hc;
            double h2 = MoonPosition.of(hoursLater(t, (double) (i + 1)), lat, lng).getAltitude() - hc;

            double a = (h0 + h2) / 2 - h1;
            double b = (h2 - h0) / 2;
            double xe = -b / (2 * a);
            ye = (a * xe + b) * xe + h1;
            double d = b * b - 4 * a * h1;
            int roots = 0;

            double x1 = 0.0, x2 = 0.0;
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

        return new MoonTimes(hoursLater(t, rise), hoursLater(t, set), ye);
    }

    private final Date rise, set;
    private final double ye;

    private MoonTimes(Date rise, Date set, double ye) {
        this.rise = rise;
        this.set = set;
        this.ye = ye;
    }

    /**
     * Moonrise time. {@code null} if the moon does not rise that day.
     */
    public Date getRise() {
        return (rise != null ? new Date(rise.getTime()) : null);
    }

    /**
     * Moonset time. {@code null} if the moon does not set that day.
     */
    public Date getSet() {
        return (set != null ? new Date(set.getTime()) : null);
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

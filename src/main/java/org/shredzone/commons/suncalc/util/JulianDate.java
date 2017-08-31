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

import static java.lang.Math.floor;
import static org.shredzone.commons.suncalc.util.ExtendedMath.PI2;

import java.util.Calendar;
import java.util.Date;

/**
 * This class contains a Julian Date representation of a date.
 * <p>
 * Objects are immutable and threadsafe.
 */
public class JulianDate {

    private final Calendar cal;
    private final double mjd;

    /**
     * Creates a new {@link JulianDate}.
     *
     * @param cal
     *            {@link Calendar} to use. Do not modify this object after invocation.
     */
    public JulianDate(Calendar cal) {
        this.cal = cal;

        int zoneOffset = cal.getTimeZone().getOffset(cal.getTimeInMillis());
        long localTime = cal.getTimeInMillis() + zoneOffset;
        mjd = localTime / 86400000.0 + 40587.0;
    }

    /**
     * Returns a {@link JulianDate} of the current date and the given hour.
     *
     * @param hour
     *            Hour of this date. This is a floating point value. Fractions are used
     *            for minutes and seconds.
     * @return {@link JulianDate} instance.
     */
    public JulianDate atHour(double hour) {
        Calendar clone = (Calendar) cal.clone();
        clone.add(Calendar.SECOND, (int) Math.round(hour * 60.0 * 60.0));
        return new JulianDate(clone);
    }

    /**
     * Returns this {@link JulianDate} as {@link Date} object.
     *
     * @return {@link Date} of this {@link JulianDate}.
     */
    public Date getDate() {
        return cal.getTime();
    }

    /**
     * Returns the Modified Julian Date.
     *
     * @return MJD
     */
    public double getModifiedJulianDate() {
        return mjd;
    }

    /**
     * Returns the Julian Centuries.
     *
     * @return Julian Centuries, based on J2000 epoch.
     */
    public double getJulianCentury() {
        return (mjd - 51544.5) / 36525.0;
    }

    /**
     * Retuns the Greenwich Mean Sidereal Time of this Julian Date.
     *
     * @return GMST
     */
    public double getGreenwichMeanSiderealTime() {
        final double secs = 86400.0;

        double mjd0 = floor(mjd);
        double ut = (mjd - mjd0) * secs;
        double t0 = (mjd0 - 51544.5) / 36525.0;
        double t = (mjd - 51544.5) / 36525.0;

        double gmst = 24110.54841
                + 8640184.812866 * t0
                + 1.0027379093 * ut
                + (0.093104 - 6.2e-6 * t) * t * t;

        return (PI2 / secs) * (gmst % secs);
    }

}

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

import java.util.Calendar;
import java.util.Date;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

import org.shredzone.commons.suncalc.param.TimeResultParameter.Unit;

/**
 * This class contains a Julian Date representation of a date.
 * <p>
 * Objects are immutable and threadsafe.
 */
@ParametersAreNonnullByDefault
@Immutable
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
        mjd = cal.getTimeInMillis() / 86400000.0 + 40587.0;
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
        Calendar clone = getCalendar();
        clone.add(Calendar.SECOND, (int) round(hour * 60.0 * 60.0));
        return new JulianDate(clone);
    }

    /**
     * Returns a {@link JulianDate} of the given modified Julian date.
     *
     * @param mjd
     *            Modified Julian Date
     * @return {@link JulianDate} instance.
     * @since 2.3
     */
    public JulianDate atModifiedJulianDate(double mjd) {
        Calendar clone = getCalendar();
        clone.setTimeInMillis(Math.round((mjd - 40587.0) * 86400000.0));
        clone.clear(Calendar.MILLISECOND);
        return new JulianDate(clone);
    }

    /**
     * Returns a {@link JulianDate} of the given Julian century.
     *
     * @param jc
     *            Julian Century
     * @return {@link JulianDate} instance.
     * @since 2.3
     */
    public JulianDate atJulianCentury(double jc) {
        return atModifiedJulianDate(jc * 36525.0 + 51544.5);
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
     * Returns this {@link JulianDate} as truncated {@link Date} object.
     *
     * @param unit
     *            {@link Unit} to truncate to
     * @return Rounded {@link Date} of this {@link JulianDate}.
     * @since 2.3
     */
    public Date getDateTruncated(Unit unit) {
        if (unit == null) { //NOSONAR: safety null check
            throw new NullPointerException();
        }

        Calendar clone = getCalendar();
        clone.set(Calendar.MILLISECOND, 0);

        if (unit == Unit.MINUTES || unit == Unit.HOURS || unit == Unit.DAYS) {
            clone.add(Calendar.SECOND, 30);
            clone.set(Calendar.SECOND, 0);
        }

        if (unit == Unit.HOURS || unit == Unit.DAYS) {
            clone.add(Calendar.MINUTE, 30);
            clone.set(Calendar.MINUTE, 0);
        }

        if (unit == Unit.DAYS) {
            clone.set(Calendar.HOUR_OF_DAY, 0);
        }

        return clone.getTime();
    }

    /**
     * Returns this {@link JulianDate} as {@link Calendar} object.
     *
     * @return New {@link Calendar} instance of this {@link JulianDate}.
     */
    public Calendar getCalendar() {
        return (Calendar) cal.clone();
    }

    /**
     * Returns the Modified Julian Date.
     *
     * @return Modified Julian Date, UTC.
     */
    public double getModifiedJulianDate() {
        return mjd;
    }

    /**
     * Returns the Julian Centuries.
     *
     * @return Julian Centuries, based on J2000 epoch, UTC.
     */
    public double getJulianCentury() {
        return (mjd - 51544.5) / 36525.0;
    }

    /**
     * Returns the Greenwich Mean Sidereal Time of this Julian Date.
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

    /**
     * Returns the earth's true anomaly of the current date.
     * <p>
     * A simple approximation is used here.
     *
     * @return True anomaly, in radians
     */
    public double getTrueAnomaly() {
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR) - 1;
        return PI2 * frac((dayOfYear - 4.0) / 365.256363);
    }

    @Override
    public String toString() {
        return String.format("%dd %02dh %02dm %02ds",
                (long) mjd,
                (long) (mjd * 24 % 24),
                (long) (mjd * 24 * 60 % 60),
                (long) (mjd * 24 * 60 * 60 % 60));
    }

}

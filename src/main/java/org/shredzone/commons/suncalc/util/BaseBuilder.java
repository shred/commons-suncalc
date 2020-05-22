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

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.ParametersAreNonnullByDefault;

import org.shredzone.commons.suncalc.param.GenericParameter;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.param.TimeResultParameter;

/**
 * A base implementation of {@link LocationParameter} and {@link TimeParameter}.
 * <p>
 * For internal use only.
 *
 * @param <T>
 *            Type of the final builder
 */
@ParametersAreNonnullByDefault
@SuppressWarnings("unchecked")
public class BaseBuilder<T> implements GenericParameter<T>, LocationParameter<T>,
        TimeParameter<T>, TimeResultParameter<T>, Cloneable {

    private double lat = 0.0;
    private double lng = 0.0;
    private double height = 0.0;
    private Calendar cal = createCalendar();
    private Unit unit = Unit.MINUTES;

    @Override
    public T on(int year, int month, int date) {
        cal.clear();
        cal.set(year, month - 1, date);
        return (T) this;
    }

    @Override
    public T on(int year, int month, int date, int hour, int minute, int second) {
        cal.clear();
        cal.set(year, month - 1, date, hour, minute, second);
        return (T) this;
    }

    @Override
    public T on(Date date) {
        if (date == null) { //NOSONAR: safety null check
            throw new NullPointerException();
        }
        cal.setTime(date);
        return (T) this;
    }

    @Override
    public T on(Calendar calendar) {
        if (calendar == null) { //NOSONAR: safety null check
            throw new NullPointerException();
        }
        on(calendar.getTime());
        timezone(calendar.getTimeZone());
        return (T) this;
    }

    @Override
    public T plusDays(int days) {
        cal.add(Calendar.DAY_OF_MONTH, days);
        return (T) this;
    }

    @Override
    public T today() {
        now();
        midnight();
        return (T) this;
    }

    @Override
    public T tomorrow() {
        today();
        plusDays(1);
        return (T) this;
    }

    @Override
    public T now() {
        return on(createCalendar());
    }

    @Override
    public T midnight() {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (T) this;
    }

    @Override
    public T timezone(TimeZone tz) {
        if (tz == null) { //NOSONAR: safety null check
            throw new NullPointerException();
        }
        cal.setTimeZone(tz);
        return (T) this;
    }

    @Override
    public T timezone(String id) {
        return timezone(TimeZone.getTimeZone(id));
    }

    @Override
    public T utc() {
        return timezone("UTC");
    }

    @Override
    public T localTime() {
        return timezone(TimeZone.getDefault());
    }

    @Override
    public T at(double lat, double lng) {
        latitude(lat);
        longitude(lng);
        return (T) this;
    }

    @Override
    public T at(double[] coords) {
        if (coords.length != 2 && coords.length != 3) {
            throw new IllegalArgumentException("Array must contain 2 or 3 doubles");
        }
        if (coords.length == 3) {
            height(coords[2]);
        }
        return at(coords[0], coords[1]);
    }

    @Override
    public T latitude(double lat) {
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException("Latitude out of range, -90.0 <= " + lat + " <= 90.0");
        }
        this.lat = lat;
        return (T) this;
    }

    @Override
    public T longitude(double lng) {
        if (lng < -180.0 || lng > 180.0) {
            throw new IllegalArgumentException("Longitude out of range, -180.0 <= " + lng + " <= 180.0");
        }
        this.lng = lng;
        return (T) this;
    }

    @Override
    public T latitude(int d, int m, double s) {
        return latitude(dms(d, m, s));
    }

    @Override
    public T longitude(int d, int m, double s) {
        return longitude(dms(d, m, s));
    }

    @Override
    public T height(double h) {
        this.height = h;
        return (T) this;
    }

    @Override
    public T truncatedTo(Unit unit) {
        if (unit == null) { //NOSONAR: safety null check
            throw new NullPointerException();
        }
        this.unit = unit;
        return (T) this;
    }

    @Override
    public T copy() {
        try {
            return (T) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex); // Should never be thrown anyway
        }
    }

    /**
     * Returns the longitude.
     *
     * @return Longitude, in degrees.
     */
    public double getLongitude() {
        return lng;
    }

    /**
     * Returns the latitude.
     *
     * @return Latitude, in degrees.
     */
    public double getLatitude() {
        return lat;
    }

    /**
     * Returns the longitude.
     *
     * @return Longitude, in radians.
     */
    public double getLongitudeRad() {
        return toRadians(lng);
    }

    /**
     * Returns the latitude.
     *
     * @return Latitude, in radians.
     */
    public double getLatitudeRad() {
        return toRadians(lat);
    }

    /**
     * Returns the height, in meters above sea level.
     *
     * @return Height, meters above sea level
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the {@link JulianDate} to be used.
     *
     * @return {@link JulianDate}
     */
    public JulianDate getJulianDate() {
        return new JulianDate((Calendar) cal.clone());
    }

    /**
     * Returns the {@link Unit} to truncate to.
     *
     * @return {@link Unit}
     * @since 2.3
     */
    public Unit getTruncatedTo() {
        return unit;
    }

    /**
     * Creates a new {@link Calendar} instance containing the current instant.
     * <p>
     * This method can be overriden on unit tests.
     *
     * @return {@link Calendar} instance
     */
    protected Calendar createCalendar() {
        return Calendar.getInstance();
    }

    /**
     * Converts dms to double.
     *
     * @param d
     *            Degrees. Sign is used for result.
     * @param m
     *            Minutes. Sign is ignored.
     * @param s
     *            Seconds and fractions. Sign is ignored.
     * @return angle, in degrees
     */
    private static double dms(int d, int m, double s) {
        double sig = d < 0 ? -1.0 : 1.0;
        return sig * ((abs(s) / 60.0 + abs(m)) / 60.0 + abs(d));
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        BaseBuilder<T> b = (BaseBuilder<T>) super.clone();
        b.cal = (Calendar) this.cal.clone();
        return b;
    }

}

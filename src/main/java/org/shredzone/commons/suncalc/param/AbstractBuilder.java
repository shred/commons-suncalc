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
package org.shredzone.commons.suncalc.param;

import static java.lang.Math.toRadians;
import static java.util.Objects.requireNonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.shredzone.commons.suncalc.util.JulianDate;

/**
 * An abstract implementation of {@link LocationParameter} and {@link TimeParameter}.
 * <p>
 * For internal use only.
 *
 * @param <T>
 *            Type of the final builder
 */
@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<T> implements LocationParameter<T>, TimeParameter<T> {

    private double lat = 0.0;
    private double lng = 0.0;
    private double height = 0.0;
    private Calendar cal = Calendar.getInstance();

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
        cal.setTime(requireNonNull(date));
        return (T) this;
    }

    @Override
    public T on(Calendar calendar) {
        requireNonNull(calendar);
        on(calendar.getTime());
        timezone(calendar.getTimeZone());
        return (T) this;
    }

    @Override
    public T today() {
        now();
        midnight();
        return (T) this;
    }

    @Override
    public T now() {
        return on(Calendar.getInstance());
    }

    @Override
    public T midnight() {
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return (T) this;
    }

    @Override
    public T timezone(TimeZone tz) {
        cal.setTimeZone(requireNonNull(tz));
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
        return latitude((s / 60.0 + m) / 60.0 + d);
    }

    @Override
    public T longitude(int d, int m, double s) {
        return longitude((s / 60.0 + m) / 60.0 + d);
    }

    @Override
    public T height(double h) {
        this.height = h;
        return (T) this;
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

}

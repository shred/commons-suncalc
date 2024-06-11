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

import static java.lang.Math.max;
import static java.lang.Math.toRadians;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.shredzone.commons.suncalc.param.GenericParameter;
import org.shredzone.commons.suncalc.param.LocationParameter;
import org.shredzone.commons.suncalc.param.TimeParameter;
import org.shredzone.commons.suncalc.param.WindowParameter;

/**
 * A base implementation of {@link LocationParameter}, {@link TimeParameter}, and
 * {@link WindowParameter}.
 * <p>
 * For internal use only.
 *
 * @param <T>
 *         Type of the final builder
 */
@SuppressWarnings("unchecked")
public class BaseBuilder<T> implements GenericParameter<T>, LocationParameter<T>,
        TimeParameter<T>, WindowParameter<T>, Cloneable {

    private @Nullable Double lat = null;
    private @Nullable Double lng = null;
    private double elevation = 0.0;
    private ZonedDateTime dateTime = ZonedDateTime.now();
    private Duration duration = Duration.ofDays(365L);

    @Override
    public T on(ZonedDateTime dateTime) {
        this.dateTime = Objects.requireNonNull(dateTime, "dateTime");
        return (T) this;
    }

    @Override
    public T on(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        return on(ZonedDateTime.of(dateTime, this.dateTime.getZone()));
    }

    @Override
    public T on(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return on(ZonedDateTime.of(date, LocalTime.MIDNIGHT, dateTime.getZone()));
    }

    @Override
    public T on(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        return on(ZonedDateTime.ofInstant(instant, dateTime.getZone()));
    }

    @Override
    public T on(int year, int month, int date, int hour, int minute, int second) {
        return on(ZonedDateTime.of(year, month, date, hour, minute, second, 0, dateTime.getZone()));
    }

    @Override
    public T now() {
        return on(ZonedDateTime.now(dateTime.getZone()));
    }

    @Override
    public T plusDays(int days) {
        return on(dateTime.plusDays(days));
    }

    @Override
    public T midnight() {
        return on(dateTime.truncatedTo(ChronoUnit.DAYS));
    }

    @Override
    public T timezone(ZoneId tz) {
        Objects.requireNonNull(tz, "tz");
        on(dateTime.withZoneSameLocal(tz));
        return (T) this;
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
    public T elevation(double h) {
        this.elevation = max(h, 0.0);
        return (T) this;
    }

    public T limit(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        if (duration.isNegative()) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.duration = duration;
        return (T) this;
    }

    @Override
    public T sameTimeAs(TimeParameter<?> t) {
        if (! (t instanceof BaseBuilder)) {
            throw new IllegalArgumentException("Cannot read the TimeParameter");
        }
        this.dateTime = ((BaseBuilder<?>) t).dateTime;
        return (T) this;
    }

    @Override
    public T sameLocationAs(LocationParameter<?> l) {
        if (! (l instanceof BaseBuilder)) {
            throw new IllegalArgumentException("Cannot read the LocationParameter");
        }
        BaseBuilder<?> origin = (BaseBuilder<?>) l;
        this.lat = origin.lat;
        this.lng = origin.lng;
        this.elevation = origin.elevation;
        return (T) this;
    }

    @Override
    public T sameWindowAs(WindowParameter<?> w) {
        if (! (w instanceof BaseBuilder)) {
            throw new IllegalArgumentException("Cannot read the WindowParameter");
        }
        BaseBuilder<?> origin = (BaseBuilder<?>) w;
        this.duration = origin.duration;
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
        if (lng == null) {
            throw new IllegalStateException("longitude is not set");
        }
        return lng;
    }

    /**
     * Returns the latitude.
     *
     * @return Latitude, in degrees.
     */
    public double getLatitude() {
        if (lat == null) {
            throw new IllegalStateException("latitude is not set");
        }
        return lat;
    }

    /**
     * Returns the longitude.
     *
     * @return Longitude, in radians.
     */
    public double getLongitudeRad() {
        return toRadians(getLongitude());
    }

    /**
     * Returns the latitude.
     *
     * @return Latitude, in radians.
     */
    public double getLatitudeRad() {
        return toRadians(getLatitude());
    }

    /**
     * Returns the elevation, in meters above sea level.
     *
     * @return Elevation, meters above sea level
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * Returns the {@link JulianDate} to be used.
     *
     * @return {@link JulianDate}
     */
    public JulianDate getJulianDate() {
        return new JulianDate(dateTime);
    }

    /**
     * Returns {@code true} if a geolocation has been set.
     *
     * @since 3.9
     */
    public boolean hasLocation() {
        return lat != null && lng != null;
    }

    /**
     * Unset the geolocation.
     *
     * @since 3.9
     */
    public void clearLocation() {
        lat = null;
        lng = null;
    }

    /**
     * Returns the duration of the time window.
     *
     * @since 3.11
     */
    public Duration getDuration() {
        return duration;
    }

}

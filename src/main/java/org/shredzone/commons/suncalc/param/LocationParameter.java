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

import static org.shredzone.commons.suncalc.util.ExtendedMath.dms;

/**
 * Location based parameters.
 * <p>
 * Use them to give information about the geolocation of the observer. If ommitted, the
 * coordinates of <a href="https://en.wikipedia.org/wiki/Null_Island">Null Island</a> are
 * used.
 *
 * @param <T>
 *            Type of the final builder
 */
@SuppressWarnings("unchecked")
public interface LocationParameter<T> {

    /**
     * Sets the latitude.
     *
     * @param lat
     *            Latitude, in degrees.
     * @return itself
     */
    T latitude(double lat);

    /**
     * Sets the longitude.
     *
     * @param lng
     *            Longitude, in degrees.
     * @return itself
     */
    T longitude(double lng);

    /**
     * Sets the elevation.
     *
     * @param h
     *            Elevation, in meters above sea level. Default: 0.0 m. Negative values
     *            are silently changed to the acceptable minimum of 0.0 m.
     * @return itself
     * @see #elevationFt(double)
     * @since 3.9
     */
    T elevation(double h);

    /**
     * Sets the elevation, in foot.
     *
     * @param ft
     *            Elevation, in foot above sea level. Default: 0.0 ft. Negative values are
     *            silently changed to the acceptable minimum of 0.0 ft.
     * @return itself
     * @see #elevation(double)
     * @since 3.9
     */
    default T elevationFt(double ft) {
        return elevation(ft * 0.3048);
    }

    /**
     * Sets the height.
     *
     * @param h
     *            Height, in meters above sea level. Default: 0.0 m. Negative values are
     *            silently changed to the acceptable minimum of 0.0 m.
     * @return itself
     * @deprecated Use {@link #elevation(double)} instead.
     */
    @Deprecated
    default T height(double h) {
        return elevation(h);
    }

    /**
     * Sets the height, in foot.
     *
     * @param ft
     *            Height, in foot above sea level. Default: 0.0 ft. Negative values are
     *            silently changed to the acceptable minimum of 0.0 ft.
     * @return itself
     * @since 3.8
     * @deprecated Use {@link #elevationFt(double)} instead.
     */
    @Deprecated
    default T heightFt(double ft) {
        return elevationFt(ft);
    }

    /**
     * Sets the geolocation.
     *
     * @param lat
     *            Latitude, in degrees.
     * @param lng
     *            Longitude, in degrees.
     * @return itself
     */
    default T at(double lat, double lng) {
        latitude(lat);
        longitude(lng);
        return (T) this;
    }

    /**
     * Sets the geolocation. In the given array, index 0 must contain the latitude, and
     * index 1 must contain the longitude. An optional index 2 may contain the elevation,
     * in meters.
     * <p>
     * This call is meant to be used for coordinates stored in constants.
     *
     * @param coords
     *            Array containing the latitude and longitude, in degrees.
     * @return itself
     */
    default T at(double[] coords) {
        if (coords.length != 2 && coords.length != 3) {
            throw new IllegalArgumentException("Array must contain 2 or 3 doubles");
        }
        if (coords.length == 3) {
            elevation(coords[2]);
        }
        return at(coords[0], coords[1]);
    }

    /**
     * Sets the latitude.
     *
     * @param d
     *            Degrees
     * @param m
     *            Minutes
     * @param s
     *            Seconds (and fraction of seconds)
     * @return itself
     */
    default T latitude(int d, int m, double s) {
        return latitude(dms(d, m, s));
    }

    /**
     * Sets the longitude.
     *
     * @param d
     *            Degrees
     * @param m
     *            Minutes
     * @param s
     *            Seconds (and fraction of seconds)
     * @return itself
     */
    default T longitude(int d, int m, double s) {
        return longitude(dms(d, m, s));
    }

    /**
     * Uses the same location as given in the {@link LocationParameter} at this moment.
     * <p>
     * Changes to the source parameter will not affect this parameter, though.
     *
     * @param l  {@link LocationParameter} to be used.
     * @return itself
     */
    T sameLocationAs(LocationParameter<?> l);

}

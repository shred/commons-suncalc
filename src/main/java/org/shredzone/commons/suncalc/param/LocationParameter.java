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


/**
 * Location based parameters.
 * <p>
 * Use them to give information about the geolocation of the observer. If ommitted, a
 * latitude and longitude of 0° is used.
 *
 * @param <T>
 *            Type of the final builder
 */

public interface LocationParameter<T> {

    /**
     * Sets the geolocation.
     *
     * @param lat
     *            Latitude, in degrees.
     * @param lng
     *            Longitude, in degrees.
     * @return itself
     */
    T at(double lat, double lng);

    /**
     * Sets the geolocation. In the given array, index 0 must contain the latitude, and
     * index 1 must contain the longitude. An optional index 2 may contain the height, in
     * meters.
     * <p>
     * This call is meant to be used for coordinates stored in constants.
     *
     * @param coords
     *            Array containing the latitude and longitude, in degrees.
     * @return itself
     */
    T at(double[] coords);

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
    T latitude(int d, int m, double s);

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
    T longitude(int d, int m, double s);

    /**
     * Sets the height.
     * <p>
     * This parameter can be safely ommitted. The height only has a very small effect on
     * moon calculations, and almost no effect on sun calculations. For reasonable
     * heights, the effect is lower than the general accuracy of this library.
     *
     * @param h
     *            Height, in meters above sea level. Default: 0.0 m
     * @return itself
     */
    T height(double h);

}

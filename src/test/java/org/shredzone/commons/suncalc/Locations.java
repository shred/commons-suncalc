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
package org.shredzone.commons.suncalc;

import java.time.ZoneId;

/**
 * Geocoordinates of some test locations.
 */
public final class Locations {

    /**
     * Cologne, Germany. A random city on the northern hemisphere.
     */
    public static final double[] COLOGNE = new double[] { 50.938056, 6.956944 };
    public static final ZoneId COLOGNE_TZ = ZoneId.of("Europe/Berlin");

    /**
     * Alert, Nunavut, Canada. The northernmost place in the world with a permanent
     * population.
     */
    public static final double[] ALERT = new double[] { 82.5, -62.316667 };
    public static final ZoneId ALERT_TZ = ZoneId.of("Canada/Eastern");

    /**
     * Wellington, New Zealand. A random city on the southern hemisphere, close to the
     * international date line.
     */
    public static final double[] WELLINGTON = new double[] { -41.2875, 174.776111 };
    public static final ZoneId WELLINGTON_TZ = ZoneId.of("Pacific/Auckland");

    /**
     * Puerto Williams, Chile. The southernmost town in the world.
     */
    public static final double[] PUERTO_WILLIAMS = new double[] { -54.933333, -67.616667 };
    public static final ZoneId PUERTO_WILLIAMS_TZ = ZoneId.of("America/Punta_Arenas");

    /**
     * Singapore. A random city close to the equator.
     */
    public static final double[] SINGAPORE = new double[] { 1.283333, 103.833333 };
    public static final ZoneId SINGAPORE_TZ = ZoneId.of("Asia/Singapore");

    /**
     * Martinique. To test a fix for issue #13.
     */
    public static final double[] MARTINIQUE = new double[] { 14.640725, -61.0112 };
    public static final ZoneId MARTINIQUE_TZ = ZoneId.of("America/Martinique");

    /**
     * Sydney. To test a fix for issue #14.
     */
    public static final double[] SYDNEY = new double[] { -33.744272, 151.231291 };
    public static final ZoneId SYDNEY_TZ = ZoneId.of("Australia/Sydney");

    /**
     * Santa Monica, CA. To test a fix for issue #18.
     */
    public static final double[] SANTA_MONICA = new double[] { 34.0, -118.5 };
    public static final ZoneId SANTA_MONICA_TZ = ZoneId.of("America/Los_Angeles");

}

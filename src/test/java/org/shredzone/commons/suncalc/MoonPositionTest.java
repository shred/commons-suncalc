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

import static org.assertj.core.api.Assertions.assertThat;
import static org.shredzone.commons.suncalc.Locations.*;

import java.util.TimeZone;

import org.assertj.core.data.Offset;
import org.junit.Test;

/**
 * Unit tests for {@link MoonPosition}.
 */
public class MoonPositionTest {

    private static final Offset<Double> ERROR = Offset.offset(0.1);
    private static final Offset<Double> DISTANCE_ERROR = Offset.offset(800.0);

    @Test
    public void testCologne() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 13, 28, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(mp1.getAzimuth()).as("azimuth").isCloseTo(304.8, ERROR);
        assertThat(mp1.getAltitude()).as("altitude").isCloseTo(-39.6, ERROR);
        assertThat(mp1.getTrueAltitude()).as("trueAltitude").isCloseTo(-39.6, ERROR);
        assertThat(mp1.getParallacticAngle()).as("pa").isCloseTo(32.0, ERROR);

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 3, 51, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(mp2.getAzimuth()).as("azimuth").isCloseTo(179.9, ERROR);
        assertThat(mp2.getAltitude()).as("altitude").isCloseTo(25.3, ERROR);
        assertThat(mp2.getTrueAltitude()).as("trueAltitude").isCloseTo(25.3, ERROR);
        assertThat(mp2.getDistance()).as("distance").isCloseTo(394709.0, DISTANCE_ERROR);
        assertThat(mp2.getParallacticAngle()).as("pa").isCloseTo(0.0, ERROR);
    }

    @Test
    public void testAlert() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 8, 4, 0)
                        .at(ALERT)
                        .timezone(ALERT_TZ)
                        .execute();
        assertThat(mp1.getAzimuth()).as("azimuth").isCloseTo(257.5, ERROR);
        assertThat(mp1.getAltitude()).as("altitude").isCloseTo(-10.9, ERROR);
        assertThat(mp1.getTrueAltitude()).as("trueAltitude").isCloseTo(-10.9, ERROR);
        assertThat(mp1.getParallacticAngle()).as("pa").isCloseTo(7.5, ERROR);

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 2, 37, 0)
                        .at(ALERT)
                        .timezone(ALERT_TZ)
                        .execute();
        assertThat(mp2.getAzimuth()).as("azimuth").isCloseTo(179.8, ERROR);
        assertThat(mp2.getAltitude()).as("altitude").isCloseTo(-5.7, ERROR);
        assertThat(mp2.getTrueAltitude()).as("trueAltitude").isCloseTo(-5.7, ERROR);
        assertThat(mp2.getDistance()).as("distance").isCloseTo(393609.0, DISTANCE_ERROR);
        assertThat(mp2.getParallacticAngle()).as("pa").isCloseTo(0.0, ERROR);
    }

    @Test
    public void testWellington() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 4, 7, 0)
                        .at(WELLINGTON)
                        .timezone(WELLINGTON_TZ)
                        .execute();
        assertThat(mp1.getAzimuth()).as("azimuth").isCloseTo(311.3, ERROR);
        assertThat(mp1.getAltitude()).as("altitude").isCloseTo(55.1, ERROR);
        assertThat(mp1.getTrueAltitude()).as("trueAltitude").isCloseTo(55.1, ERROR);
        assertThat(mp1.getParallacticAngle()).as("pa").isCloseTo(144.2, ERROR);

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 2, 17, 0)
                        .at(WELLINGTON)
                        .timezone(WELLINGTON_TZ)
                        .execute();
        assertThat(mp2.getAzimuth()).as("azimuth").isCloseTo(0.5, ERROR);
        assertThat(mp2.getAltitude()).as("altitude").isCloseTo(63.9, ERROR);
        assertThat(mp2.getTrueAltitude()).as("trueAltitude").isCloseTo(63.9, ERROR);
        assertThat(mp2.getDistance()).as("distance").isCloseTo(396272.0, DISTANCE_ERROR);
        assertThat(mp2.getParallacticAngle()).as("pa").isCloseTo(-179.6, ERROR);
    }

    @Test
    public void testPuertoWilliams() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 2, 7, 9, 44, 0)
                        .at(PUERTO_WILLIAMS)
                        .timezone(PUERTO_WILLIAMS_TZ)
                        .execute();
        assertThat(mp1.getAzimuth()).as("azimuth").isCloseTo(199.4, ERROR);
        assertThat(mp1.getAltitude()).as("altitude").isCloseTo(-52.7, ERROR);
        assertThat(mp1.getTrueAltitude()).as("trueAltitude").isCloseTo(-52.7, ERROR);
        assertThat(mp1.getParallacticAngle()).as("pa").isCloseTo(168.3, ERROR);

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 2, 7, 23, 4, 0)
                        .at(PUERTO_WILLIAMS)
                        .timezone(PUERTO_WILLIAMS_TZ)
                        .execute();
        assertThat(mp2.getAzimuth()).as("azimuth").isCloseTo(0.1, ERROR);
        assertThat(mp2.getAltitude()).as("altitude").isCloseTo(16.3, ERROR);
        assertThat(mp2.getTrueAltitude()).as("trueAltitude").isCloseTo(16.3, ERROR);
        assertThat(mp2.getDistance()).as("distance").isCloseTo(369731.0, DISTANCE_ERROR);
        assertThat(mp2.getParallacticAngle()).as("pa").isCloseTo(-179.9, ERROR);
    }

    @Test
    public void testSingapore() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 5, 12, 0)
                        .at(SINGAPORE)
                        .timezone(SINGAPORE_TZ)
                        .execute();
        assertThat(mp1.getAzimuth()).as("azimuth").isCloseTo(240.6, ERROR);
        assertThat(mp1.getAltitude()).as("altitude").isCloseTo(57.1, ERROR);
        assertThat(mp1.getTrueAltitude()).as("trueAltitude").isCloseTo(57.1, ERROR);
        assertThat(mp1.getParallacticAngle()).as("pa").isCloseTo(64.0, ERROR);

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 3, 11, 0)
                        .at(SINGAPORE)
                        .timezone(SINGAPORE_TZ)
                        .execute();
        assertThat(mp2.getAzimuth()).as("azimuth").isCloseTo(180.0, ERROR);
        assertThat(mp2.getAltitude()).as("altitude").isCloseTo(74.1, ERROR);
        assertThat(mp2.getTrueAltitude()).as("trueAltitude").isCloseTo(74.1, ERROR);
        assertThat(mp2.getDistance()).as("distance").isCloseTo(395621.0, DISTANCE_ERROR);
        assertThat(mp2.getParallacticAngle()).as("pa").isCloseTo(0.0, ERROR);
    }

    @Test
    public void testBaghdad() {
        MoonPosition mp1 = MoonPosition.compute()
                .on(2002, 12, 4, 16, 57, 0)
                .latitude(33, 20, 0.0)
                .longitude(44, 25, 0.0)
                .elevationFt(100.0)
                .timezone("Asia/Baghdad")
                .execute();
        assertThat(mp1.getAzimuth()).as("azimuth").isCloseTo(241.1, ERROR);
        assertThat(mp1.getAltitude()).as("altitude").isCloseTo(1.2, ERROR);
        assertThat(mp1.getTrueAltitude()).as("trueAltitude").isCloseTo(0.8, ERROR);
        assertThat(mp1.getParallacticAngle()).as("pa").isCloseTo(52.8, ERROR);
    }

}

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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.shredzone.commons.suncalc.Locations.*;

import org.junit.Test;

/**
 * Unit tests for {@link MoonPosition}.
 */
public class MoonPositionTest {

    private static final double ERROR = 0.1;

    @Test
    public void testCologne() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 13, 28, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("azimuth", mp1.getAzimuth(), is(closeTo(304.8, ERROR)));
        assertThat("altitude", mp1.getAltitude(), is(closeTo(-39.6, ERROR)));

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 3, 51, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("azimuth", mp2.getAzimuth(), is(closeTo(179.9, ERROR)));
        assertThat("altitude", mp2.getAltitude(), is(closeTo(25.3, ERROR)));
        assertThat("distance", mp2.getDistance(), is(closeTo(391626.1, ERROR)));
    }

    @Test
    public void testAlert() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 8, 4, 0)
                        .at(ALERT)
                        .timezone(ALERT_TZ)
                        .execute();
        assertThat("azimuth", mp1.getAzimuth(), is(closeTo(257.5, ERROR)));
        assertThat("altitude", mp1.getAltitude(), is(closeTo(-10.9, ERROR)));

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 2, 37, 0)
                        .at(ALERT)
                        .timezone(ALERT_TZ)
                        .execute();
        assertThat("azimuth", mp2.getAzimuth(), is(closeTo(179.8, ERROR)));
        assertThat("altitude", mp2.getAltitude(), is(closeTo(-5.7, ERROR)));
        assertThat("distance", mp2.getDistance(), is(closeTo(390721.7, ERROR)));
    }

    @Test
    public void testWellington() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 4, 7, 0)
                        .at(WELLINGTON)
                        .timezone(WELLINGTON_TZ)
                        .execute();
        assertThat("azimuth", mp1.getAzimuth(), is(closeTo(311.3, ERROR)));
        assertThat("altitude", mp1.getAltitude(), is(closeTo(55.1, ERROR)));

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 2, 17, 0)
                        .at(WELLINGTON)
                        .timezone(WELLINGTON_TZ)
                        .execute();
        assertThat("azimuth", mp2.getAzimuth(), is(closeTo(0.5, ERROR)));
        assertThat("altitude", mp2.getAltitude(), is(closeTo(63.9, ERROR)));
        assertThat("distance", mp2.getDistance(), is(closeTo(393760.7, ERROR)));
    }

    @Test
    public void testPuertoWilliams() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 2, 7, 9, 44, 0)
                        .at(PUERTO_WILLIAMS)
                        .timezone(PUERTO_WILLIAMS_TZ)
                        .execute();
        assertThat("azimuth", mp1.getAzimuth(), is(closeTo(199.4, ERROR)));
        assertThat("altitude", mp1.getAltitude(), is(closeTo(-52.7, ERROR)));

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 2, 7, 23, 4, 0)
                        .at(PUERTO_WILLIAMS)
                        .timezone(PUERTO_WILLIAMS_TZ)
                        .execute();
        assertThat("azimuth", mp2.getAzimuth(), is(closeTo(0.1, ERROR)));
        assertThat("altitude", mp2.getAltitude(), is(closeTo(16.3, ERROR)));
        assertThat("distance", mp2.getDistance(), is(closeTo(368900.3, ERROR)));
    }

    @Test
    public void testSingapore() {
        MoonPosition mp1 = MoonPosition.compute()
                        .on(2017, 7, 12, 5, 12, 0)
                        .at(SINGAPORE)
                        .timezone(SINGAPORE_TZ)
                        .execute();
        assertThat("azimuth", mp1.getAzimuth(), is(closeTo(240.6, ERROR)));
        assertThat("altitude", mp1.getAltitude(), is(closeTo(57.1, ERROR)));

        MoonPosition mp2 = MoonPosition.compute()
                        .on(2017, 7, 12, 3, 11, 0)
                        .at(SINGAPORE)
                        .timezone(SINGAPORE_TZ)
                        .execute();
        assertThat("azimuth", mp2.getAzimuth(), is(closeTo(180.0, ERROR)));
        assertThat("altitude", mp2.getAltitude(), is(closeTo(74.1, ERROR)));
        assertThat("distance", mp2.getDistance(), is(closeTo(392867.9, ERROR)));
    }

}

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
 * Unit tests for {@link SunPosition}.
 */
public class SunPositionTest {

    private static final double ERROR = 0.1;

    @Test
    public void testCologne() {
        SunPosition sp1 = SunPosition.compute()
                        .on(2017, 7, 12, 16, 10, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("azimuth", sp1.getAzimuth(), is(closeTo(239.8, ERROR)));
        assertThat("altitude", sp1.getAltitude(), is(closeTo(48.6, ERROR)));

        SunPosition sp2 = SunPosition.compute()
                        .on(2017, 7, 12, 13, 37, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("azimuth", sp2.getAzimuth(), is(closeTo(179.6, ERROR)));
        assertThat("altitude", sp2.getAltitude(), is(closeTo(61.0, ERROR)));
    }

    @Test
    public void testAlert() {
        SunPosition sp1 = SunPosition.compute()
                        .on(2017, 7, 12, 6, 17, 0)
                        .at(ALERT)
                        .timezone(ALERT_TZ)
                        .execute();
        assertThat("azimuth", sp1.getAzimuth(), is(closeTo(87.5, ERROR)));
        assertThat("altitude", sp1.getAltitude(), is(closeTo(21.8, ERROR)));

        SunPosition sp2 = SunPosition.compute()
                        .on(2017, 7, 12, 12, 14, 0)
                        .at(ALERT)
                        .timezone(ALERT_TZ)
                        .execute();
        assertThat("azimuth", sp2.getAzimuth(), is(closeTo(179.7, ERROR)));
        assertThat("altitude", sp2.getAltitude(), is(closeTo(29.4, ERROR)));
    }

    @Test
    public void testWellington() {
        SunPosition sp1 = SunPosition.compute()
                        .on(2017, 7, 12, 3, 7, 0)
                        .at(WELLINGTON)
                        .timezone(WELLINGTON_TZ)
                        .execute();
        assertThat("azimuth", sp1.getAzimuth(), is(closeTo(107.3, ERROR)));
        assertThat("altitude", sp1.getAltitude(), is(closeTo(-51.3, ERROR)));

        SunPosition sp2 = SunPosition.compute()
                        .on(2017, 7, 12, 12, 26, 0)
                        .at(WELLINGTON)
                        .timezone(WELLINGTON_TZ)
                        .execute();
        assertThat("azimuth", sp2.getAzimuth(), is(closeTo(0.1, ERROR)));
        assertThat("altitude", sp2.getAltitude(), is(closeTo(26.8, ERROR)));
    }

    @Test
    public void testPuertoWilliams() {
        SunPosition sp1 = SunPosition.compute()
                        .on(2017, 2, 7, 18, 13, 0)
                        .at(PUERTO_WILLIAMS)
                        .timezone(PUERTO_WILLIAMS_TZ)
                        .execute();
        assertThat("azimuth", sp1.getAzimuth(), is(closeTo(280.1, ERROR)));
        assertThat("altitude", sp1.getAltitude(), is(closeTo(25.4, ERROR)));

        SunPosition sp2 = SunPosition.compute()
                        .on(2017, 2, 7, 13, 44, 0)
                        .at(PUERTO_WILLIAMS)
                        .timezone(PUERTO_WILLIAMS_TZ)
                        .execute();
        assertThat("azimuth", sp2.getAzimuth(), is(closeTo(0.2, ERROR)));
        assertThat("altitude", sp2.getAltitude(), is(closeTo(50.2, ERROR)));
    }

    @Test
    public void testSingapore() {
        SunPosition sp1 = SunPosition.compute()
                        .on(2017, 7, 12, 10, 19, 0)
                        .at(SINGAPORE)
                        .timezone(SINGAPORE_TZ)
                        .execute();
        assertThat("azimuth", sp1.getAzimuth(), is(closeTo(60.4, ERROR)));
        assertThat("altitude", sp1.getAltitude(), is(closeTo(43.5, ERROR)));

        SunPosition sp2 = SunPosition.compute()
                        .on(2017, 7, 12, 13, 10, 0)
                        .at(SINGAPORE)
                        .timezone(SINGAPORE_TZ)
                        .execute();
        assertThat("azimuth", sp2.getAzimuth(), is(closeTo(0.2, ERROR)));
        assertThat("altitude", sp2.getAltitude(), is(closeTo(69.4, ERROR)));
    }

    @Test
    public void testDistance() {
        SunPosition sp1 = SunPosition.compute()
                        .on(2017, 1, 4, 12, 37, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(sp1.getDistance(), is(closeTo(147097390.6, ERROR)));

        SunPosition sp2 = SunPosition.compute()
                        .on(2017, 4, 20, 13, 31, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(sp2.getDistance(), is(closeTo(150181373.3, ERROR)));

        SunPosition sp3 = SunPosition.compute()
                        .on(2017, 7, 12, 13, 37, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(sp3.getDistance(), is(closeTo(152088309.0, ERROR)));

        SunPosition sp4 = SunPosition.compute()
                        .on(2017, 10, 11, 13, 18, 0)
                        .at(COLOGNE)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(sp4.getDistance(), is(closeTo(149380680.0, ERROR)));
    }

}

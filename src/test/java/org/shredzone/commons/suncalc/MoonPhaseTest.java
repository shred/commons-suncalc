/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2018 Richard "Shred" Körber
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

import java.time.temporal.ChronoUnit;

import org.assertj.core.api.AbstractDateAssert;
import org.assertj.core.data.Offset;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shredzone.commons.suncalc.MoonPhase.Phase;

/**
 * Unit tests for {@link MoonPhase}.
 */
public class MoonPhaseTest {

    private static final Offset<Double> ERROR = Offset.offset(500.0);

    @BeforeClass
    public static void init() {
        AbstractDateAssert.registerCustomDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    }

    @Test
    public void testNewMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.NEW_MOON)
                        .execute();

        assertThat(mp.getTime().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo("2017-09-20T05:29:30Z");
        assertThat(mp.getDistance()).isCloseTo(382740.0, ERROR);
    }

    @Test
    public void testFirstQuarterMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.FIRST_QUARTER)
                        .execute();

        assertThat(mp.getTime().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo("2017-09-28T02:52:40Z");
        assertThat(mp.getDistance()).isCloseTo(403894.0, ERROR);
    }

    @Test
    public void testFullMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.FULL_MOON)
                        .execute();

        assertThat(mp.getTime().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo("2017-09-06T07:07:44Z");
        assertThat(mp.getDistance()).isCloseTo(384364.0, ERROR);
    }

    @Test
    public void testLastQuarterMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.LAST_QUARTER)
                        .execute();

        assertThat(mp.getTime().truncatedTo(ChronoUnit.SECONDS))
                .isEqualTo("2017-09-13T06:28:34Z");
        assertThat(mp.getDistance()).isCloseTo(369899.0, ERROR);
    }

    @Test
    public void testToPhase() {
        // exact angles
        assertThat(Phase.toPhase(  0.0)).isEqualTo(Phase.NEW_MOON);
        assertThat(Phase.toPhase( 45.0)).isEqualTo(Phase.WAXING_CRESCENT);
        assertThat(Phase.toPhase( 90.0)).isEqualTo(Phase.FIRST_QUARTER);
        assertThat(Phase.toPhase(135.0)).isEqualTo(Phase.WAXING_GIBBOUS);
        assertThat(Phase.toPhase(180.0)).isEqualTo(Phase.FULL_MOON);
        assertThat(Phase.toPhase(225.0)).isEqualTo(Phase.WANING_GIBBOUS);
        assertThat(Phase.toPhase(270.0)).isEqualTo(Phase.LAST_QUARTER);
        assertThat(Phase.toPhase(315.0)).isEqualTo(Phase.WANING_CRESCENT);

        // out of range angles (normalization test)
        assertThat(Phase.toPhase( 360.0)).isEqualTo(Phase.NEW_MOON);
        assertThat(Phase.toPhase( 720.0)).isEqualTo(Phase.NEW_MOON);
        assertThat(Phase.toPhase(-360.0)).isEqualTo(Phase.NEW_MOON);
        assertThat(Phase.toPhase(-720.0)).isEqualTo(Phase.NEW_MOON);
        assertThat(Phase.toPhase( 855.0)).isEqualTo(Phase.WAXING_GIBBOUS);
        assertThat(Phase.toPhase(-585.0)).isEqualTo(Phase.WAXING_GIBBOUS);
        assertThat(Phase.toPhase(-945.0)).isEqualTo(Phase.WAXING_GIBBOUS);

        // close to boundary
        assertThat(Phase.toPhase( 22.4)).isEqualTo(Phase.NEW_MOON);
        assertThat(Phase.toPhase( 67.4)).isEqualTo(Phase.WAXING_CRESCENT);
        assertThat(Phase.toPhase(112.4)).isEqualTo(Phase.FIRST_QUARTER);
        assertThat(Phase.toPhase(157.4)).isEqualTo(Phase.WAXING_GIBBOUS);
        assertThat(Phase.toPhase(202.4)).isEqualTo(Phase.FULL_MOON);
        assertThat(Phase.toPhase(247.4)).isEqualTo(Phase.WANING_GIBBOUS);
        assertThat(Phase.toPhase(292.4)).isEqualTo(Phase.LAST_QUARTER);
        assertThat(Phase.toPhase(337.4)).isEqualTo(Phase.WANING_CRESCENT);
        assertThat(Phase.toPhase(382.4)).isEqualTo(Phase.NEW_MOON);
    }

}

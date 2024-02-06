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

import org.assertj.core.data.Offset;
import org.junit.Test;
import org.shredzone.commons.suncalc.MoonPhase.Phase;

/**
 * Unit tests for {@link MoonIllumination}.
 */
public class MoonIlluminationTest {

    private static final Offset<Double> ERROR = Offset.offset(0.1);
    private static final Offset<Double> FINE_ERROR = Offset.offset(0.001);

    @Test
    public void testNewMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 6, 24, 4, 30, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(mi.getFraction()).as("fraction").isCloseTo(0.0, ERROR);
        assertThat(mi.getPhase()).as("phase").isCloseTo(176.0, ERROR); // -180.0
        assertThat(mi.getAngle()).as("angle").isCloseTo(2.0, ERROR);
        assertThat(mi.getClosestPhase()).as("MoonPhase.Phase").isEqualTo(Phase.NEW_MOON);
    }

    @Test
    public void testWaxingHalfMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 7, 1, 2, 51, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(mi.getFraction()).as("fraction").isCloseTo(0.5, ERROR);
        assertThat(mi.getPhase()).as("phase").isCloseTo(-90.0, ERROR);
        assertThat(mi.getAngle()).as("angle").isCloseTo(-66.9, ERROR);
        assertThat(mi.getClosestPhase()).as("MoonPhase.Phase").isEqualTo(Phase.FIRST_QUARTER);
    }

    @Test
    public void testFullMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 7, 9, 6, 6, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(mi.getFraction()).as("fraction").isCloseTo(1.0, ERROR);
        assertThat(mi.getPhase()).as("phase").isCloseTo(-3.2, ERROR); // 0.0
        assertThat(mi.getAngle()).as("angle").isCloseTo(-7.4, ERROR);
        assertThat(mi.getClosestPhase()).as("MoonPhase.Phase").isEqualTo(Phase.FULL_MOON);
    }

    @Test
    public void testWaningHalfMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 7, 16, 21, 25, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat(mi.getFraction()).as("fraction").isCloseTo(0.5, ERROR);
        assertThat(mi.getPhase()).as("phase").isCloseTo(90.0, ERROR);
        assertThat(mi.getAngle()).as("angle").isCloseTo(68.7, ERROR);
        assertThat(mi.getClosestPhase()).as("MoonPhase.Phase").isEqualTo(Phase.LAST_QUARTER);
    }

    @Test
    public void testBaghdad1() {
        MoonIllumination tmi = MoonIllumination.compute()
                .on(2002, 12, 6, 17, 49, 0)
                .timezone(BAGHDAD_TZ)
                .at(BAGHDAD)
                .elevation(46.0)
                .execute();
        assertThat(tmi.getElongation()).as("elongation").isCloseTo(29.8, ERROR);
        assertThat(tmi.getRadius()).as("moonRadius").isCloseTo(0.2661, FINE_ERROR);
        assertThat(tmi.getCrescentWidth()).as("crescentWidth").isCloseTo(0.036, FINE_ERROR);
    }

    @Test
    public void testBaghdad2() {
        MoonIllumination tmi = MoonIllumination.compute()
                .on(2002, 9, 8, 17, 49, 0)
                .timezone(BAGHDAD_TZ)
                .at(BAGHDAD)
                .elevation(46.0)
                .execute();
        assertThat(tmi.getElongation()).as("elongation").isCloseTo(20.2, ERROR);
        assertThat(tmi.getRadius()).as("moonRadius").isCloseTo(0.278, FINE_ERROR);
        assertThat(tmi.getCrescentWidth()).as("crescentWidth").isCloseTo(0.017, FINE_ERROR);
    }

    @Test
    public void testCapeTown1() {
        MoonIllumination tmi = MoonIllumination.compute()
                .on(2010, 7, 13, 18, 51, 0)
                .timezone(CAPETOWN_TZ)
                .at(CAPETOWN)
                .execute();
        assertThat(tmi.getElongation()).as("elongation").isCloseTo(25.3, ERROR);
        assertThat(tmi.getRadius()).as("moonRadius").isCloseTo(0.276, FINE_ERROR);
        assertThat(tmi.getCrescentWidth()).as("crescentWidth").isCloseTo(0.027, FINE_ERROR);
    }

    @Test
    public void testCapeTown2() {
        MoonIllumination tmi = MoonIllumination.compute()
                .on(2010, 2, 16, 20, 1, 0)
                .timezone(CAPETOWN_TZ)
                .at(CAPETOWN)
                .execute();
        assertThat(tmi.getElongation()).as("elongation").isCloseTo(28.7, ERROR);
        assertThat(tmi.getRadius()).as("moonRadius").isCloseTo(0.248, FINE_ERROR);
        assertThat(tmi.getCrescentWidth()).as("crescentWidth").isCloseTo(0.031, FINE_ERROR);
    }

}

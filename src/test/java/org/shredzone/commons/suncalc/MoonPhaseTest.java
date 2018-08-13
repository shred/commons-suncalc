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

import org.assertj.core.api.AbstractDateAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shredzone.commons.suncalc.MoonPhase.Phase;

/**
 * Unit tests for {@link MoonPhase}.
 */
public class MoonPhaseTest {

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

        assertThat(mp.getTime()).isEqualTo("2017-09-20T05:30:00Z");
    }

    @Test
    public void testFirstQuarterMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.FIRST_QUARTER)
                        .execute();

        assertThat(mp.getTime()).isEqualTo("2017-09-28T02:53:00Z");
    }

    @Test
    public void testFullMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.FULL_MOON)
                        .execute();

        assertThat(mp.getTime()).isEqualTo("2017-09-06T07:08:00Z");
    }

    @Test
    public void testLastQuarterMoon() {
        MoonPhase mp = MoonPhase.compute()
                        .on(2017, 9, 1)
                        .utc()
                        .phase(Phase.LAST_QUARTER)
                        .execute();

        assertThat(mp.getTime()).isEqualTo("2017-09-13T06:29:00Z");
    }

}

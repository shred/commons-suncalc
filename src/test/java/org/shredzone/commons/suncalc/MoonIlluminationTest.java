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
import static org.shredzone.commons.suncalc.Locations.COLOGNE_TZ;

import org.junit.Test;

/**
 * Unit tests for {@link MoonIllumination}.
 */
public class MoonIlluminationTest {

    private static final double ERROR = 0.1;

    @Test
    public void testNewMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 6, 24, 4, 30, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("fraction", mi.getFraction(), is(closeTo(0.0, ERROR)));
        assertThat("phase", mi.getPhase(), is(closeTo(175.9, ERROR))); // -180.0
        assertThat("angle", mi.getAngle(), is(closeTo(2.0, ERROR)));
    }

    @Test
    public void testWaxingHalfMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 7, 1, 2, 51, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("fraction", mi.getFraction(), is(closeTo(0.5, ERROR)));
        assertThat("phase", mi.getPhase(), is(closeTo(-89.9, ERROR))); // -90.0
        assertThat("angle", mi.getAngle(), is(closeTo(-66.9, ERROR)));
    }

    @Test
    public void testFullMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 7, 9, 6, 6, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("fraction", mi.getFraction(), is(closeTo(1.0, ERROR)));
        assertThat("phase", mi.getPhase(), is(closeTo(-3.1, ERROR))); // 0.0
        assertThat("angle", mi.getAngle(), is(closeTo(-7.4, ERROR)));
    }

    @Test
    public void testWaningHalfMoon() {
        MoonIllumination mi = MoonIllumination.compute()
                        .on(2017, 7, 16, 21, 25, 0)
                        .timezone(COLOGNE_TZ)
                        .execute();
        assertThat("fraction", mi.getFraction(), is(closeTo(0.5, ERROR)));
        assertThat("phase", mi.getPhase(), is(closeTo(89.8, ERROR))); // 90.0
        assertThat("angle", mi.getAngle(), is(closeTo(68.7, ERROR)));
    }

}

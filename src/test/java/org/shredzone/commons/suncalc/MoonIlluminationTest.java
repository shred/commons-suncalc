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

import org.junit.Test;

/**
 * Unit tests for {@link MoonIllumination}.
 */
public class MoonIlluminationTest {

    private static final double ERROR = 0.000_000_001;

    @Test
    public void testMoonIllumination() {
        MoonIllumination moonIllum = MoonIllumination.compute().on(2013, 3, 5).utc().execute();
        assertThat("fraction", moonIllum.getFraction(), is(closeTo(0.491425328, ERROR)));
        assertThat("phase", moonIllum.getPhase(), is(closeTo(0.752729536, ERROR)));
        assertThat("angle", moonIllum.getAngle(), is(closeTo(96.06, 0.001)));
    }

}

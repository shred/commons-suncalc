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
package org.shredzone.commons.suncalc.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.shredzone.commons.suncalc.util.ExtendedMath.*;

import org.junit.Test;

/**
 * Unit tests for {@link ExtendedMath}.
 */
public class ExtendedMathTest {

    private static final double ERROR = 0.001;

    @Test
    public void testFrac() {
        assertThat(frac(   1.0 ), is(closeTo( 0.0 , ERROR)));
        assertThat(frac(   0.5 ), is(closeTo( 0.5 , ERROR)));
        assertThat(frac( 123.25), is(closeTo( 0.25, ERROR)));
        assertThat(frac(   0.0 ), is(closeTo( 0.0 , ERROR)));
        assertThat(frac(  -1.0 ), is(closeTo( 0.0 , ERROR)));
        assertThat(frac(  -0.5 ), is(closeTo(-0.5 , ERROR)));
        assertThat(frac(-123.25), is(closeTo(-0.25, ERROR)));
    }

    @Test
    public void testIsZero() {
        assertThat(isZero( 1.0   ), is(false));
        assertThat(isZero( 0.0001), is(false));
        assertThat(isZero( 0.0   ), is(true));
        assertThat(isZero(-0.0   ), is(true));
        assertThat(isZero(-0.0001), is(false));
        assertThat(isZero(-1.0   ), is(false));
        assertThat(isZero( Double.NaN), is(false));
        assertThat(isZero(-Double.NaN), is(false));
        assertThat(isZero( Double.POSITIVE_INFINITY), is(false));
        assertThat(isZero( Double.NEGATIVE_INFINITY), is(false));
    }

}

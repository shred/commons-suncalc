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

import static org.assertj.core.api.Assertions.assertThat;
import static org.shredzone.commons.suncalc.util.ExtendedMath.*;

import org.assertj.core.data.Offset;
import org.junit.Test;

/**
 * Unit tests for {@link ExtendedMath}.
 */
public class ExtendedMathTest {

    private static final Offset<Double> ERROR = Offset.offset(0.001);

    @Test
    public void testFrac() {
        assertThat(frac(   1.0 )).isCloseTo( 0.0 , ERROR);
        assertThat(frac(   0.5 )).isCloseTo( 0.5 , ERROR);
        assertThat(frac( 123.25)).isCloseTo( 0.25, ERROR);
        assertThat(frac(   0.0 )).isCloseTo( 0.0 , ERROR);
        assertThat(frac(  -1.0 )).isCloseTo( 0.0 , ERROR);
        assertThat(frac(  -0.5 )).isCloseTo(-0.5 , ERROR);
        assertThat(frac(-123.25)).isCloseTo(-0.25, ERROR);
    }

    @Test
    public void testIsZero() {
        assertThat(isZero( 1.0   )).isFalse();
        assertThat(isZero( 0.0001)).isFalse();
        assertThat(isZero( 0.0   )).isTrue();
        assertThat(isZero(-0.0   )).isTrue();
        assertThat(isZero(-0.0001)).isFalse();
        assertThat(isZero(-1.0   )).isFalse();
        assertThat(isZero( Double.NaN)).isFalse();
        assertThat(isZero(-Double.NaN)).isFalse();
        assertThat(isZero( Double.POSITIVE_INFINITY)).isFalse();
        assertThat(isZero( Double.NEGATIVE_INFINITY)).isFalse();
    }

    @Test
    public void testDms() {
        // Valid parameters
        assertThat(dms(  0,   0,   0.0 )).isEqualTo(0.0);
        assertThat(dms( 13,  27,   4.32)).isEqualTo(13.4512);
        assertThat(dms(-88,  39,   8.28)).isEqualTo(-88.6523);

        // Sign at wrong position is ignored
        assertThat(dms( 14, -14,   2.4)).isEqualTo(14.234);
        assertThat(dms( 66,  12, -46.8)).isEqualTo(66.213);

        // Out of range values are carried to the next position
        assertThat(dms(  0,   0,  72.0)).isEqualTo(0.02);   // 0°  1' 12.0"
        assertThat(dms(  1,  80, 132.0)).isEqualTo(2.37);   // 2° 22' 12.0"
    }

}

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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link Vector}.
 */
public class VectorTest {

    @Test
    public void toPolarTest() {
        double PI_HALF = Math.PI / 2.0;

        Vector v1 = new Vector(20.0, 0.0, 0.0);
        assertThat(v1.getPhi(), is(0.0));
        assertThat(v1.getTheta(), is(0.0));
        assertThat(v1.getR(), is(20.0));

        Vector v2 = new Vector(0.0, 20.0, 0.0);
        assertThat(v2.getPhi(), is(PI_HALF));
        assertThat(v2.getTheta(), is(0.0));
        assertThat(v2.getR(), is(20.0));

        Vector v3 = new Vector(0.0, 0.0, 20.0);
        assertThat(v3.getPhi(), is(0.0));
        assertThat(v3.getTheta(), is(PI_HALF));
        assertThat(v3.getR(), is(20.0));

        Vector v4 = new Vector(-20.0, 0.0, 0.0);
        assertThat(v4.getPhi(), is(Math.PI));
        assertThat(v4.getTheta(), is(0.0));
        assertThat(v4.getR(), is(20.0));

        Vector v5 = new Vector(0.0, -20.0, 0.0);
        assertThat(v5.getPhi(), is(Math.PI + PI_HALF));
        assertThat(v5.getTheta(), is(0.0));
        assertThat(v5.getR(), is(20.0));

        Vector v6 = new Vector(0.0, 0.0, -20.0);
        assertThat(v6.getPhi(), is(0.0));
        assertThat(v6.getTheta(), is(-PI_HALF));
        assertThat(v6.getR(), is(20.0));
    }

}

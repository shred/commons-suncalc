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

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Unit tests for {@link JulianDate}.
 */
public class JulianDateTest {

    @Test
    public void mjdTest() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(2017, 7, 19, 15, 6, 16);

        JulianDate jd = new JulianDate(cal);
        assertThat(jd.getModifiedJulianDate(), is(57984.62935185185));
    }

}

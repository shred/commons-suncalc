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

import static java.lang.Math.PI;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;
import org.shredzone.commons.suncalc.DateMatcher;

/**
 * Unit tests for {@link JulianDate}.
 */
public class JulianDateTest {

    private static final double ERROR = 0.001;

    @Test
    public void testAtHour() {
        JulianDate jd = new JulianDate(of(2017, 8, 19, 0, 0, 0, "UTC"));
        assertDate(jd, "2017-08-19T00:00:00Z", "UTC");

        JulianDate jd2 = jd.atHour(8.5);
        assertDate(jd2, "2017-08-19T08:30:00Z", "UTC");

        JulianDate jd3 = new JulianDate(of(2017, 8, 19, 0, 0, 0, "Europe/Berlin"));
        assertDate(jd3, "2017-08-19T00:00:00+0200", "Europe/Berlin");

        JulianDate jd4 = jd3.atHour(8.5);
        assertDate(jd4, "2017-08-19T08:30:00+0200", "Europe/Berlin");
    }

    @Test
    public void testDateGetters() {
        Calendar cal = of(2017, 8, 19, 0, 0, 0, "UTC");
        JulianDate jd = new JulianDate(cal);

        assertThat(jd.getCalendar(), is(cal));
        assertThat(jd.getDate().getTime(), is(cal.getTimeInMillis()));
    }

    @Test
    public void testModifiedJulianDate() {
        // MJD epoch is midnight of November 17th, 1858.
        JulianDate jd1 = new JulianDate(of(1858, 11, 17, 0, 0, 0, "UTC"));
        assertThat(jd1.getModifiedJulianDate(), is(0.0));
        assertThat(jd1.toString(), is("0d 00h 00m 00s"));

        JulianDate jd2 = new JulianDate(of(2017, 8, 19, 15, 6, 16, "UTC"));
        assertThat(jd2.getModifiedJulianDate(), is(closeTo(57984.629, ERROR)));
        assertThat(jd2.toString(), is("57984d 15h 06m 16s"));

        JulianDate jd3 = new JulianDate(of(2017, 8, 19, 15, 6, 16, "GMT+2"));
        assertThat(jd3.getModifiedJulianDate(), is(closeTo(57984.546, ERROR)));
        assertThat(jd3.toString(), is("57984d 13h 06m 16s"));
    }

    @Test
    public void testJulianCentury() {
        JulianDate jd1 = new JulianDate(of(2000, 1, 1, 0, 0, 0, "UTC"));
        assertThat(jd1.getJulianCentury(), is(closeTo(0.0, ERROR)));

        JulianDate jd2 = new JulianDate(of(2017, 1, 1, 0, 0, 0, "UTC"));
        assertThat(jd2.getJulianCentury(), is(closeTo(0.17, ERROR)));

        JulianDate jd3 = new JulianDate(of(2050, 7, 1, 0, 0, 0, "UTC"));
        assertThat(jd3.getJulianCentury(), is(closeTo(0.505, ERROR)));
    }

    @Test
    public void testGreenwichMeanSiderealTime() {
        JulianDate jd1 = new JulianDate(of(2017, 9, 3, 19, 5, 15, "UTC"));
        assertThat(jd1.getGreenwichMeanSiderealTime(), is(closeTo(4.702, ERROR)));

    }

    @Test
    public void testTrueAnomaly() {
        JulianDate jd1 = new JulianDate(of(2017, 1, 4, 0, 0, 0, "UTC"));
        assertThat(jd1.getTrueAnomaly(), is(closeTo(0.0, 0.1)));

        JulianDate jd2 = new JulianDate(of(2017, 7, 4, 0, 0, 0, "UTC"));
        assertThat(jd2.getTrueAnomaly(), is(closeTo(PI, 0.1)));
    }

    private Calendar of(int year, int month, int day, int hour, int minute, int second, String zone) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(zone));
        cal.clear();
        cal.set(year, month - 1, day, hour, minute, second);
        return cal;
    }

    private void assertDate(JulianDate jd, String date, String tz) {
        assertThat(jd.getCalendar().getTime(), DateMatcher.is(date, tz));
    }

}

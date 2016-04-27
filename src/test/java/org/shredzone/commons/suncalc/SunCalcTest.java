/*
 * Shredzone Commons - suncalc
 *
 * Copyright (C) 2016 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Bases on SunCalc by Vladimir Agafonkin (https://github.com/mourner/suncalc)
 */
package org.shredzone.commons.suncalc;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * Unit tests.
 *
 * @author Richard "Shred" Körber
 */
public class SunCalcTest {

    private static final double LAT = 50.5;
    private static final double LNG = 30.5;

    @Test
    public void testSunPosition() {
        Date date = createDate(2013, 3, 5); // 2013-03-05 00:00:00 UTC
        SunPosition sunPos = SunPosition.of(date, LAT, LNG);

        assertThat(sunPos.getAzimuth(), is(-2.5003175907168385));
        assertThat(sunPos.getAltitude(), is(-0.7000406838781611));
    }

    @Test
    public void testSunTimes() {
        Map<String, Matcher<Date>> testTimes = new HashMap<>();
        testTimes.put("sunrise",       DateMatcher.is("2013-03-05T04:34:56Z"));
        testTimes.put("sunset",        DateMatcher.is("2013-03-05T15:46:57Z"));
        testTimes.put("sunriseEnd",    DateMatcher.is("2013-03-05T04:38:19Z"));
        testTimes.put("sunsetStart",   DateMatcher.is("2013-03-05T15:43:34Z"));
        testTimes.put("dawn",          DateMatcher.is("2013-03-05T04:02:17Z"));
        testTimes.put("dusk",          DateMatcher.is("2013-03-05T16:19:36Z"));
        testTimes.put("nauticalDawn",  DateMatcher.is("2013-03-05T03:24:31Z"));
        testTimes.put("nauticalDusk",  DateMatcher.is("2013-03-05T16:57:22Z"));
        testTimes.put("nightEnd",      DateMatcher.is("2013-03-05T02:46:17Z"));
        testTimes.put("night",         DateMatcher.is("2013-03-05T17:35:36Z"));
        testTimes.put("goldenHourEnd", DateMatcher.is("2013-03-05T05:19:01Z"));
        testTimes.put("goldenHour",    DateMatcher.is("2013-03-05T15:02:52Z"));
        testTimes.put("solarNoon",     DateMatcher.is("2013-03-05T10:10:57Z"));
        testTimes.put("nadir",         DateMatcher.is("2013-03-04T22:10:57Z"));

        Date date = createDate(2013, 3, 5); // 2013-03-05 00:00:00 UTC

        SunTimes times = SunTimes.of(date, LAT, LNG);
        Map<String, Date> timeMap = times.getTimes();

        assertThat(timeMap.size(), is(testTimes.size()));
        for (String key : testTimes.keySet()) {
            assertThat(key, timeMap.get(key), testTimes.get(key));
        }
    }

    @Test
    public void testMoonPosition() {
        Date date = createDate(2013, 3, 5); // 2013-03-05 00:00:00 UTC

        MoonPosition moonPos = MoonPosition.of(date, LAT, LNG);

        assertThat(moonPos.getAzimuth(), is(-0.9783999522438226));
        assertThat(moonPos.getAltitude(), is(0.014551482243892251));
        assertThat(moonPos.getDistance(), is(364121.37256256194));
    }

    @Test
    public void testMoonIllumination() {
        Date date = createDate(2013, 3, 5); // 2013-03-05 00:00:00 UTC

        MoonIllumination moonIllum = MoonIllumination.of(date);

        assertThat(moonIllum.getFraction(), is(0.4848068202456374));
        assertThat(moonIllum.getPhase(), is(0.7548368838538762));
        assertThat(moonIllum.getAngle(), is(1.6732942678578346));
    }

    @Test
    public void testMoonTimes() {
        Date date = createDate(2013, 3, 4); // 2013-03-04 00:00:00 UTC

        MoonTimes moonTimes = MoonTimes.ofUTC(date, LAT, LNG);

        assertThat("rise", moonTimes.getRise(), DateMatcher.is("2013-03-04T23:54:29Z"));
        assertThat("set", moonTimes.getSet(), DateMatcher.is("2013-03-04T07:47:58Z"));
    }

    private static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }

}

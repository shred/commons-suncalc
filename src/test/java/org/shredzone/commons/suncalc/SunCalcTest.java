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
import java.util.EnumMap;
import java.util.Map;
import java.util.TimeZone;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.shredzone.commons.suncalc.SunTimes.Time;

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
        Map<Time, Matcher<Date>> testTimes = new EnumMap<>(Time.class);
        testTimes.put(Time.SUNRISE,         DateMatcher.is("2013-03-05T04:34:56Z"));
        testTimes.put(Time.SUNRISE_END,     DateMatcher.is("2013-03-05T04:38:19Z"));
        testTimes.put(Time.GOLDEN_HOUR_END, DateMatcher.is("2013-03-05T05:19:01Z"));
        testTimes.put(Time.SOLAR_NOON,      DateMatcher.is("2013-03-05T10:10:57Z"));
        testTimes.put(Time.GOLDEN_HOUR,     DateMatcher.is("2013-03-05T15:02:52Z"));
        testTimes.put(Time.SUNSET_START,    DateMatcher.is("2013-03-05T15:43:34Z"));
        testTimes.put(Time.SUNSET,          DateMatcher.is("2013-03-05T15:46:57Z"));
        testTimes.put(Time.DUSK,            DateMatcher.is("2013-03-05T16:19:36Z"));
        testTimes.put(Time.NAUTICAL_DUSK,   DateMatcher.is("2013-03-05T16:57:22Z"));
        testTimes.put(Time.NIGHT,           DateMatcher.is("2013-03-05T17:35:36Z"));
        testTimes.put(Time.NADIR,           DateMatcher.is("2013-03-04T22:10:57Z"));
        testTimes.put(Time.NIGHT_END,       DateMatcher.is("2013-03-05T02:46:17Z"));
        testTimes.put(Time.NAUTICAL_DAWN,   DateMatcher.is("2013-03-05T03:24:31Z"));
        testTimes.put(Time.DAWN,            DateMatcher.is("2013-03-05T04:02:17Z"));

        Date date = createDate(2013, 3, 5); // 2013-03-05 00:00:00 UTC

        SunTimes times = SunTimes.of(date, LAT, LNG);

        for (Time time : Time.values()) {
            assertThat(time.name(), times.getTime(time), testTimes.get(time));
        }

        assertThat(times.sunriseTime(Time.SUNRISE.getAngle()), DateMatcher.is("2013-03-05T04:34:56Z"));
        assertThat(times.sunsetTime(Time.SUNSET.getAngle()), DateMatcher.is("2013-03-05T15:46:57Z"));
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

        assertThat("rise", moonTimes.getRise(), DateMatcher.is("2013-03-04T23:53:00Z"));
        assertThat("set", moonTimes.getSet(), DateMatcher.is("2013-03-04T07:43:00Z"));

        // Cologne, Germany
        MoonTimes mt = MoonTimes.ofUTC(createDate(2017, 7, 12), 50.938056d, 6.956944d);
        assertThat("rise", mt.getRise(), DateMatcher.is("2017-07-12T21:26:00Z"));
        assertThat("set", mt.getSet(), DateMatcher.is("2017-07-12T06:53:00Z"));

        // Alert, Nunavut, Canada
        MoonTimes mt2 = MoonTimes.ofUTC(createDate(2017, 7, 12), 82.5d, -62.316667d);
        assertThat(mt2.isAlwaysUp(), is(false));
        assertThat(mt2.isAlwaysDown(), is(true));

        // Alert, Nunavut, Canada
        MoonTimes mt3 = MoonTimes.ofUTC(createDate(2017, 7, 14), 82.5d, -62.316667d);
        assertThat("rise", mt3.getRise(), DateMatcher.is("2017-07-14T05:46:00Z"));
        assertThat("set", mt3.getSet(), DateMatcher.is("2017-07-14T11:26:00Z"));

        // Wellington, New Zealand
        MoonTimes mt4 = MoonTimes.ofUTC(createDate(2017, 7, 12), -41.2875d, 174.776111d);
        assertThat("rise", mt4.getRise(), DateMatcher.is("2017-07-12T08:06:00Z"));
        assertThat("set", mt4.getSet(), DateMatcher.is("2017-07-12T21:57:00Z"));

        // Puerto Williams, Chile
        MoonTimes mt5 = MoonTimes.ofUTC(createDate(2017, 7, 13), -54.933333d, -67.616667d);
        assertThat("rise", mt5.getRise(), DateMatcher.is("2017-07-13T00:31:00Z"));
        assertThat("set", mt5.getSet(), DateMatcher.is("2017-07-13T14:48:00Z"));
    }

    private static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(year, month - 1, day);
        return cal.getTime();
    }
}

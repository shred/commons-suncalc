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

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.shredzone.commons.suncalc.SunTimes.Twilight;

/**
 * Unit tests.
 */
public class SunCalcTest {

    private static final double ERROR = 0.000_000_001;

    private static final double LAT = 50.5;
    private static final double LNG = 30.5;

    @Test
    public void testSunPosition() {
        SunPosition sunPos = SunPosition.compute().on(2013, 3, 5).utc().at(LAT, LNG).execute();
        assertThat("azimuth", sunPos.getAzimuth(), is(closeTo(216.942412096, ERROR)));
        assertThat("altitude", sunPos.getAltitude(), is(closeTo(-39.94989479, ERROR)));

        SunPosition sunPos2 = SunPosition.compute().on(2017, 8, 20, 8, 0, 0).utc().at(LAT, LNG).execute();
        assertThat("azimuth", sunPos2.getAzimuth(), is(closeTo(316.190369197, ERROR)));
        assertThat("altitude", sunPos2.getAltitude(), is(closeTo(44.530636355, ERROR)));
    }

    @Test
    public void testSunTimes() {
        Map<Twilight, Matcher<Date>> riseTimes = new EnumMap<>(Twilight.class);
        riseTimes.put(Twilight.ASTRONOMICAL, DateMatcher.is("2013-03-05T02:44:56Z"));
        riseTimes.put(Twilight.NAUTICAL,     DateMatcher.is("2013-03-05T03:23:22Z"));
        riseTimes.put(Twilight.CIVIL,        DateMatcher.is("2013-03-05T04:00:55Z"));
        riseTimes.put(Twilight.BLUE_HOUR,    DateMatcher.is("2013-03-05T04:13:23Z"));
        riseTimes.put(Twilight.VISUAL,    DateMatcher.is("2013-03-05T04:33:20Z"));
        riseTimes.put(Twilight.VISUAL_LOWER,    DateMatcher.is("2013-03-05T04:36:44Z"));
        riseTimes.put(Twilight.HORIZON,      DateMatcher.is("2013-03-05T04:38:41Z"));
        riseTimes.put(Twilight.GOLDEN_HOUR,  DateMatcher.is("2013-03-05T05:17:40Z"));

        Map<Twilight, Matcher<Date>> setTimes = new EnumMap<>(Twilight.class);
        setTimes.put(Twilight.GOLDEN_HOUR,   DateMatcher.is("2013-03-05T15:02:16Z"));
        setTimes.put(Twilight.HORIZON,       DateMatcher.is("2013-03-05T15:41:12Z"));
        setTimes.put(Twilight.VISUAL_LOWER,     DateMatcher.is("2013-03-05T15:43:09Z"));
        setTimes.put(Twilight.VISUAL,     DateMatcher.is("2013-03-05T15:46:31Z"));
        setTimes.put(Twilight.BLUE_HOUR,     DateMatcher.is("2013-03-05T16:06:20Z"));
        setTimes.put(Twilight.CIVIL,         DateMatcher.is("2013-03-05T16:18:52Z"));
        setTimes.put(Twilight.NAUTICAL,      DateMatcher.is("2013-03-05T16:56:49Z"));
        setTimes.put(Twilight.ASTRONOMICAL,  DateMatcher.is("2013-03-05T17:35:19Z"));

        for (Twilight angle : Twilight.values()) {
            SunTimes times = SunTimes.compute().at(LAT, LNG).on(2013, 3, 5).utc().twilight(angle).execute();
            assertThat(angle.name() + "-rise", times.getRise(), riseTimes.get(angle));
            assertThat(angle.name() + "-set", times.getSet(), setTimes.get(angle));
            assertThat("noon", times.getNoon(), DateMatcher.is("2013-03-05T10:08:33Z"));
            assertThat("nadir", times.getNadir(), DateMatcher.is("2013-03-05T22:07:10Z"));
        }
    }

    @Test
    public void testMoonPosition() {
        MoonPosition moonPos = MoonPosition.compute().at(LAT, LNG).on(2013, 3, 5).utc().execute();
        assertThat("azimuth", moonPos.getAzimuth(), is(closeTo(304.661536751, ERROR)));
        assertThat("altitude", moonPos.getAltitude(), is(closeTo(1.340077257, ERROR)));
        assertThat("distance", moonPos.getDistance(), is(closeTo(364120.978084165, ERROR)));
    }

    @Test
    public void testMoonIllumination() {
        MoonIllumination moonIllum = MoonIllumination.compute().on(2013, 3, 5).utc().execute();
        assertThat("fraction", moonIllum.getFraction(), is(closeTo(0.491425328, ERROR)));
        assertThat("phase", moonIllum.getPhase(), is(closeTo(0.752729536, ERROR)));
        assertThat("angle", moonIllum.getAngle(), is(closeTo(96.059469727, ERROR)));
    }

    @Test
    public void testMoonTimes() {
        MoonTimes moonTimes = MoonTimes.compute().at(LAT, LNG).on(2013, 3, 4).utc().execute();
        assertThat("rise", moonTimes.getRise(), DateMatcher.is("2013-03-04T23:53:35Z"));
        assertThat("set", moonTimes.getSet(), DateMatcher.is("2013-03-04T07:42:20Z"));

        // Cologne, Germany
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 12).utc().at(50.938056d, 6.956944d).execute();
        assertThat("rise", mt.getRise(), DateMatcher.is("2017-07-12T21:25:55Z"));
        assertThat("set", mt.getSet(), DateMatcher.is("2017-07-12T06:53:20Z"));

        // Alert, Nunavut, Canada
        MoonTimes mt2 = MoonTimes.compute().on(2017, 7, 12).utc().at(82.5d, -62.316667d).execute();
        assertThat("alwaysup", mt2.isAlwaysUp(), is(false));
        assertThat("alwaysdown", mt2.isAlwaysDown(), is(true));

        // Alert, Nunavut, Canada
        MoonTimes mt3 = MoonTimes.compute().on(2017, 7, 14).utc().at(82.5d, -62.316667d).execute();
        assertThat("rise", mt3.getRise(), DateMatcher.is("2017-07-14T05:45:28Z"));
        assertThat("set", mt3.getSet(), DateMatcher.is("2017-07-14T11:26:24Z"));

        // Wellington, New Zealand
        MoonTimes mt4 = MoonTimes.compute().on(2017, 7, 12).utc().at(-41.2875d, 174.776111d).execute();
        assertThat("rise", mt4.getRise(), DateMatcher.is("2017-07-12T08:05:49Z"));
        assertThat("set", mt4.getSet(), DateMatcher.is("2017-07-12T21:57:38Z"));

        // Puerto Williams, Chile
        MoonTimes mt5 = MoonTimes.compute().on(2017, 7, 13).utc().at(-54.933333d, -67.616667d).execute();
        assertThat("rise", mt5.getRise(), DateMatcher.is("2017-07-13T00:31:12Z"));
        assertThat("set", mt5.getSet(), DateMatcher.is("2017-07-13T14:48:23Z"));
    }

}

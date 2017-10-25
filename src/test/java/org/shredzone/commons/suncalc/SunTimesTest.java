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
import static org.shredzone.commons.suncalc.Locations.*;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.shredzone.commons.suncalc.SunTimes.Twilight;

/**
 * Unit tests for {@link SunTimes}.
 */
public class SunTimesTest {

    @Test
    public void testCologne() {
        Map<Twilight, Matcher<Date>> riseTimes = new EnumMap<>(Twilight.class);
        riseTimes.put(Twilight.ASTRONOMICAL, DateMatcher.is("2017-08-10T01:44:18Z"));
        riseTimes.put(Twilight.NAUTICAL,     DateMatcher.is("2017-08-10T02:44:33Z"));
        riseTimes.put(Twilight.CIVIL,        DateMatcher.is("2017-08-10T03:34:01Z"));
        riseTimes.put(Twilight.BLUE_HOUR,    DateMatcher.is("2017-08-10T03:48:59Z"));
        riseTimes.put(Twilight.VISUAL,       DateMatcher.is("2017-08-10T04:11:36Z"));
        riseTimes.put(Twilight.VISUAL_LOWER, DateMatcher.is("2017-08-10T04:15:17Z"));
        riseTimes.put(Twilight.HORIZON,      DateMatcher.is("2017-08-10T04:17:27Z"));
        riseTimes.put(Twilight.GOLDEN_HOUR,  DateMatcher.is("2017-08-10T04:58:31Z"));

        Map<Twilight, Matcher<Date>> setTimes = new EnumMap<>(Twilight.class);
        setTimes.put(Twilight.GOLDEN_HOUR,   DateMatcher.is("2017-08-10T18:15:34Z"));
        setTimes.put(Twilight.HORIZON,       DateMatcher.is("2017-08-10T18:56:26Z"));
        setTimes.put(Twilight.VISUAL_LOWER,  DateMatcher.is("2017-08-10T18:58:37Z"));
        setTimes.put(Twilight.VISUAL,        DateMatcher.is("2017-08-10T19:02:20Z"));
        setTimes.put(Twilight.BLUE_HOUR,     DateMatcher.is("2017-08-10T19:25:16Z"));
        setTimes.put(Twilight.CIVIL,         DateMatcher.is("2017-08-10T19:40:13Z"));
        setTimes.put(Twilight.NAUTICAL,      DateMatcher.is("2017-08-10T20:28:24Z"));
        setTimes.put(Twilight.ASTRONOMICAL,  DateMatcher.is("2017-08-10T21:28:43Z"));

        for (Twilight angle : Twilight.values()) {
            SunTimes times = SunTimes.compute().at(COLOGNE).on(2017, 8, 10).utc()
                            .twilight(angle).execute();
            assertThat(angle.name() + "-rise", times.getRise(), riseTimes.get(angle));
            assertThat(angle.name() + "-set", times.getSet(), setTimes.get(angle));
            assertThat("noon", times.getNoon(), DateMatcher.is("2017-08-10T11:37:57Z"));
            assertThat("nadir", times.getNadir(), DateMatcher.is("2017-08-10T23:37:59Z"));
            assertThat("always-down", times.isAlwaysDown(), is(false));
            assertThat("always-up", times.isAlwaysUp(), is(false));
        }

        SunTimes times = SunTimes.compute().at(COLOGNE).on(2017, 8, 10).utc()
                        .twilight(-4.0).execute();
        assertThat("rise", times.getRise(), DateMatcher.is("2017-08-10T03:48:59Z"));
        assertThat("set", times.getSet(), DateMatcher.is("2017-08-10T19:25:16Z"));
        assertThat("noon", times.getNoon(), DateMatcher.is("2017-08-10T11:37:57Z"));
        assertThat("nadir", times.getNadir(), DateMatcher.is("2017-08-10T23:37:59Z"));
        assertThat("always-down", times.isAlwaysDown(), is(false));
        assertThat("always-up", times.isAlwaysUp(), is(false));
    }

    @Test
    public void testAlert() {
        SunTimes t1 = SunTimes.compute().at(ALERT).on(2017, 8, 10).utc().execute();
        assertTimes(t1, null, null, "2017-08-10T16:12:47Z");

        SunTimes t2 = SunTimes.compute().at(ALERT).on(2017, 9, 24).utc().execute();
        assertTimes(t2, "2017-09-24T09:54:29Z", "2017-09-24T22:01:58Z", "2017-09-24T16:00:23Z");

        SunTimes t3 = SunTimes.compute().at(ALERT).on(2017, 2, 10).utc().execute();
        assertTimes(t3, null, null, null);

        SunTimes t4 = SunTimes.compute().at(ALERT).on(2017, 8, 10).utc().fullCycle().execute();
        assertTimes(t4, "2017-09-06T05:13:15Z", "2017-09-06T03:06:02Z", "2017-09-05T16:05:21Z");

        SunTimes t5 = SunTimes.compute().at(ALERT).on(2017, 2, 10).utc().fullCycle().execute();
        assertTimes(t5, "2017-02-27T15:24:18Z", "2017-02-27T17:23:46Z", "2017-02-27T16:23:41Z");

        SunTimes t6 = SunTimes.compute().at(ALERT).on(2017, 9, 6).utc().oneDay().execute();
        assertTimes(t6, "2017-09-06T05:13:15Z", "2017-09-06T03:06:02Z", "2017-09-06T16:04:59Z");
    }

    @Test
    public void testWellington() {
        SunTimes t1 = SunTimes.compute().at(WELLINGTON).on(2017, 8, 10).utc().execute();
        assertTimes(t1, "2017-08-10T19:17:16Z", "2017-08-10T05:34:50Z", "2017-08-10T00:26:26Z");
    }

    @Test
    public void testPuertoWilliams() {
        SunTimes t1 = SunTimes.compute().at(PUERTO_WILLIAMS).on(2017, 8, 10).utc().execute();
        assertTimes(t1, "2017-08-10T12:01:48Z", "2017-08-10T21:10:36Z", "2017-08-10T16:36:12Z");
    }

    @Test
    public void testSingapore() {
        SunTimes t1 = SunTimes.compute().at(SINGAPORE).on(2017, 8, 10).utc().execute();
        assertTimes(t1, "2017-08-10T23:05:06Z", "2017-08-10T11:14:56Z", "2017-08-10T05:08:44Z");
    }

    private void assertTimes(SunTimes t, String rise, String set, String noon) {
        if (rise != null) {
            assertThat("sunrise", t.getRise(), DateMatcher.is(rise));
        } else {
            assertThat("sunrise", t.getRise(), is(nullValue()));
        }

        if (set != null) {
            assertThat("sunset", t.getSet(), DateMatcher.is(set));
        } else {
            assertThat("sunset", t.getSet(), is(nullValue()));
        }

        if (noon != null) {
            assertThat("noon", t.getNoon(), DateMatcher.is(noon));
        } else {
            assertThat("noon", t.getNoon(), is(nullValue()));
        }

        assertThat("always-down", t.isAlwaysDown(), is(rise == null && set == null && noon == null));
        assertThat("always-up", t.isAlwaysUp(), is(rise == null && set == null && noon != null));
    }

}

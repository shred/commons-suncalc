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

import static org.assertj.core.api.Assertions.assertThat;
import static org.shredzone.commons.suncalc.Locations.*;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.TimeZone;

import org.assertj.core.api.AbstractDateAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shredzone.commons.suncalc.SunTimes.Twilight;
import org.shredzone.commons.suncalc.param.TimeResultParameter.Unit;

/**
 * Unit tests for {@link SunTimes}.
 */
public class SunTimesTest {

    @BeforeClass
    public static void init() {
        AbstractDateAssert.registerCustomDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    }

    @Test
    public void testCologne() {
        Map<Twilight, String> riseTimes = new EnumMap<>(Twilight.class);
        riseTimes.put(Twilight.ASTRONOMICAL, "2017-08-10T01:44:18Z");
        riseTimes.put(Twilight.NAUTICAL,     "2017-08-10T02:44:57Z");
        riseTimes.put(Twilight.CIVIL,        "2017-08-10T03:34:01Z");
        riseTimes.put(Twilight.BLUE_HOUR,    "2017-08-10T03:48:59Z");
        riseTimes.put(Twilight.VISUAL,       "2017-08-10T04:11:49Z");
        riseTimes.put(Twilight.VISUAL_LOWER, "2017-08-10T04:15:33Z");
        riseTimes.put(Twilight.HORIZON,      "2017-08-10T04:17:44Z");
        riseTimes.put(Twilight.GOLDEN_HOUR,  "2017-08-10T04:58:33Z");

        Map<Twilight, String> setTimes = new EnumMap<>(Twilight.class);
        setTimes.put(Twilight.GOLDEN_HOUR,   "2017-08-10T18:15:49Z");
        setTimes.put(Twilight.HORIZON,       "2017-08-10T18:56:30Z");
        setTimes.put(Twilight.VISUAL_LOWER,  "2017-08-10T18:58:39Z");
        setTimes.put(Twilight.VISUAL,        "2017-08-10T19:02:20Z");
        setTimes.put(Twilight.BLUE_HOUR,     "2017-08-10T19:25:16Z");
        setTimes.put(Twilight.CIVIL,         "2017-08-10T19:40:13Z");
        setTimes.put(Twilight.NAUTICAL,      "2017-08-10T20:28:56Z");
        setTimes.put(Twilight.ASTRONOMICAL,  "2017-08-10T21:28:43Z");

        for (Twilight angle : Twilight.values()) {
            SunTimes times = SunTimes.compute().at(COLOGNE).on(2017, 8, 10).utc()
                            .twilight(angle)
                            .truncatedTo(Unit.SECONDS).execute();
            assertThat(times.getRise()).as("%s-rise", angle.name()).isEqualTo(riseTimes.get(angle));
            assertThat(times.getSet()).as("%s-set", angle.name()).isEqualTo(setTimes.get(angle));
            assertThat(times.getNoon()).as("noon").isEqualTo("2017-08-10T11:37:38Z");
            assertThat(times.getNadir()).as("nadir").isEqualTo("2017-08-10T23:37:59Z");
            assertThat(times.isAlwaysDown()).as("always-down").isFalse();
            assertThat(times.isAlwaysUp()).as("always-up").isFalse();
        }

        SunTimes times = SunTimes.compute().at(COLOGNE).on(2017, 8, 10).utc()
                        .twilight(-4.0)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(times.getRise()).as("rise").isEqualTo("2017-08-10T03:48:59Z");
        assertThat(times.getSet()).as("set").isEqualTo("2017-08-10T19:25:16Z");
        assertThat(times.getNoon()).as("noon").isEqualTo("2017-08-10T11:37:38Z");
        assertThat(times.getNadir()).as("nadir").isEqualTo("2017-08-10T23:37:59Z");
        assertThat(times.isAlwaysDown()).as("always-down").isFalse();
        assertThat(times.isAlwaysUp()).as("always-up").isFalse();
    }

    @Test
    public void testAlert() {
        SunTimes t1 = SunTimes.compute().at(ALERT).on(2017, 8, 10).utc()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t1, null, null, "2017-08-10T16:12:47Z", true);

        SunTimes t2 = SunTimes.compute().at(ALERT).on(2017, 9, 24).utc()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t2, "2017-09-24T09:54:29Z", "2017-09-24T22:02:01Z", "2017-09-24T15:59:16Z");

        SunTimes t3 = SunTimes.compute().at(ALERT).on(2017, 2, 10).utc()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t3, null, null, "2017-02-10T16:25:05Z", false);

        SunTimes t4 = SunTimes.compute().at(ALERT).on(2017, 8, 10).utc()
                        .fullCycle().truncatedTo(Unit.SECONDS).execute();
        assertTimes(t4, "2017-09-06T05:13:15Z", "2017-09-06T03:06:02Z", "2017-08-10T16:12:47Z", true);

        SunTimes t5 = SunTimes.compute().at(ALERT).on(2017, 2, 10).utc()
                        .fullCycle().truncatedTo(Unit.SECONDS).execute();
        assertTimes(t5, "2017-02-27T15:24:18Z", "2017-02-27T17:23:46Z", "2017-02-10T16:25:05Z", false);

        SunTimes t6 = SunTimes.compute().at(ALERT).on(2017, 9, 6).utc().oneDay()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t6, "2017-09-06T05:13:15Z", "2017-09-06T03:06:02Z", "2017-09-06T16:04:59Z");
    }

    @Test
    public void testWellington() {
        SunTimes t1 = SunTimes.compute().at(WELLINGTON).on(2017, 8, 10).utc()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t1, "2017-08-10T19:17:16Z", "2017-08-10T05:34:50Z", "2017-08-10T00:26:26Z");
    }

    @Test
    public void testPuertoWilliams() {
        SunTimes t1 = SunTimes.compute().at(PUERTO_WILLIAMS).on(2017, 8, 10).utc()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t1, "2017-08-10T12:01:51Z", "2017-08-10T21:10:36Z", "2017-08-10T16:36:12Z");
    }

    @Test
    public void testSingapore() {
        SunTimes t1 = SunTimes.compute().at(SINGAPORE).on(2017, 8, 10).utc()
                        .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t1, "2017-08-10T23:05:06Z", "2017-08-10T11:14:56Z", "2017-08-10T05:08:44Z");
    }

    @Test
    public void testMartinique() {
        SunTimes t1 = SunTimes.compute().at(MARTINIQUE).on(2019, 7, 1).utc()
                .truncatedTo(Unit.SECONDS).execute();
        assertTimes(t1, "2019-07-01T09:38:35Z", "2019-07-01T22:37:23Z", "2019-07-01T16:06:08Z");
    }

    @Test
    public void testSydney() {
        SunTimes t1 = SunTimes.compute().at(SYDNEY).on(2019, 7, 3).timezone(SYDNEY_TZ)
                .truncatedTo(Unit.SECONDS).fullCycle().execute();
        assertTimes(t1, "2019-07-02T21:00:35Z", "2019-07-03T06:58:02Z", "2019-07-03T01:59:18Z");
    }

    @Test
    public void testSequence() {
        long acceptableError = 62 * 1000L;

        Date riseBefore = createDate(2017, 11, 25, 7, 4);
        Date riseAfter = createDate(2017, 11, 26, 7, 6);
        Date setBefore = createDate(2017, 11, 25, 15, 33);
        Date setAfter = createDate(2017, 11, 26, 15, 32);

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                SunTimes times = SunTimes.compute()
                            .at(COLOGNE)
                            .on(2017, 11, 25, hour, minute, 0).utc()
                            .fullCycle()
                            .truncatedTo(Unit.SECONDS)
                            .execute();

                Date rise = times.getRise();
                Date set = times.getSet();

                assertThat(rise).isNotNull();
                assertThat(set).isNotNull();

                if (hour < 7 || (hour == 7 && minute <= 4)) {
                    long diff = Math.abs(rise.getTime() - riseBefore.getTime());
                    assertThat(diff).as("rise @%02d:%02d", hour, minute).isLessThan(acceptableError);
                } else {
                    long diff = Math.abs(rise.getTime() - riseAfter.getTime());
                    assertThat(diff).as("rise @%02d:%02d", hour, minute).isLessThan(acceptableError);
                }

                if (hour < 15 || (hour == 15 && minute <= 33)) {
                    long diff = Math.abs(set.getTime() - setBefore.getTime());
                    assertThat(diff).as("set @%02d:%02d", hour, minute).isLessThan(acceptableError);
                } else {
                    long diff = Math.abs(set.getTime() - setAfter.getTime());
                    assertThat(diff).as("set @%02d:%02d", hour, minute).isLessThan(acceptableError);
                }
            }
        }
    }

    private Date createDate(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(year, month - 1, day, hour, minute, 0);
        return cal.getTime();
    }

    private void assertTimes(SunTimes t, String rise, String set, String noon) {
        assertTimes(t, rise, set, noon, null);
    }

    private void assertTimes(SunTimes t, String rise, String set, String noon, Boolean alwaysUp) {
        if (rise != null) {
            assertThat(t.getRise()).as("sunrise").isEqualTo(rise);
        } else {
            assertThat(t.getRise()).as("sunrise").isNull();
        }

        if (set != null) {
            assertThat(t.getSet()).as("sunset").isEqualTo(set);
        } else {
            assertThat(t.getSet()).as("sunset").isNull();
        }

        assertThat(t.getNoon()).as("noon").isEqualTo(noon);

        if (alwaysUp != null) {
            assertThat(t.isAlwaysDown()).as("always-down").isNotEqualTo(alwaysUp);
            assertThat(t.isAlwaysUp()).as("always-up").isEqualTo(alwaysUp);
        } else {
            assertThat(t.isAlwaysDown()).as("always-down").isFalse();
            assertThat(t.isAlwaysUp()).as("always-up").isFalse();
        }
    }

}

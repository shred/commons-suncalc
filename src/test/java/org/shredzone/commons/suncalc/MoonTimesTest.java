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

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Unit tests for {@link MoonTimes}.
 */
public class MoonTimesTest {

    @Test
    public void testCologne() {
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 12).utc().at(COLOGNE).execute();
        assertThat("rise", mt.getRise(), DateMatcher.is("2017-07-12T21:25:58Z"));
        assertThat("set", mt.getSet(), DateMatcher.is("2017-07-12T06:53:19Z"));
        assertThat("alwaysup", mt.isAlwaysUp(), is(false));
        assertThat("alwaysdown", mt.isAlwaysDown(), is(false));
    }

    @Test
    public void testAlert() {
        MoonTimes mt1 = MoonTimes.compute().on(2017, 7, 12).utc().at(ALERT).execute();
        assertThat("alwaysup", mt1.isAlwaysUp(), is(false));
        assertThat("alwaysdown", mt1.isAlwaysDown(), is(true));

        MoonTimes mt2 = MoonTimes.compute().on(2017, 7, 12).utc().at(ALERT).fullCycle().execute();
        assertThat("rise", mt2.getRise(), DateMatcher.is("2017-07-14T05:45:33Z"));
        assertThat("set", mt2.getSet(), DateMatcher.is("2017-07-14T11:26:12Z"));
        assertThat("alwaysup", mt2.isAlwaysUp(), is(false));
        assertThat("alwaysdown", mt2.isAlwaysDown(), is(false));

        MoonTimes mt3 = MoonTimes.compute().on(2017, 7, 14).utc().at(ALERT).execute();
        assertThat("rise", mt3.getRise(), DateMatcher.is("2017-07-14T05:45:33Z"));
        assertThat("set", mt3.getSet(), DateMatcher.is("2017-07-14T11:26:12Z"));
        assertThat("alwaysup", mt3.isAlwaysUp(), is(false));
        assertThat("alwaysdown", mt3.isAlwaysDown(), is(false));

        MoonTimes mt4 = MoonTimes.compute().on(2017, 7, 18).utc().at(ALERT).oneDay().execute();
        assertThat("alwaysup", mt4.isAlwaysUp(), is(true));
        assertThat("alwaysdown", mt4.isAlwaysDown(), is(false));

        MoonTimes mt5 = MoonTimes.compute().on(2017, 7, 18).utc().at(ALERT).fullCycle().execute();
        assertThat("rise", mt5.getRise(), DateMatcher.is("2017-07-27T11:59:07Z"));
        assertThat("set", mt5.getSet(), DateMatcher.is("2017-07-27T04:07:14Z"));
        assertThat("alwaysup", mt5.isAlwaysUp(), is(false));
        assertThat("alwaysdown", mt5.isAlwaysDown(), is(false));
    }

    @Test
    public void testWellington() {
        MoonTimes mt1 = MoonTimes.compute().on(2017, 7, 12).utc().at(WELLINGTON).execute();
        assertThat("rise", mt1.getRise(), DateMatcher.is("2017-07-12T08:05:50Z"));
        assertThat("set", mt1.getSet(), DateMatcher.is("2017-07-12T21:57:35Z"));

        MoonTimes mt2 = MoonTimes.compute().on(2017, 7, 12).timezone("NZ").at(WELLINGTON).execute();
        assertThat("rise", mt2.getRise(), DateMatcher.is("2017-07-12T20:05:50+1200", "NZ"));
        assertThat("set", mt2.getSet(), DateMatcher.is("2017-07-12T09:22:59+1200", "NZ"));
    }

    @Test
    public void testPuertoWilliams() {
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 13).utc().at(PUERTO_WILLIAMS).execute();
        assertThat("rise", mt.getRise(), DateMatcher.is("2017-07-13T00:31:12Z"));
        assertThat("set", mt.getSet(), DateMatcher.is("2017-07-13T14:48:21Z"));
    }

    @Test
    public void testSingapore() {
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 13).utc().at(SINGAPORE).execute();
        assertThat("rise", mt.getRise(), DateMatcher.is("2017-07-13T14:35:11Z"));
        assertThat("set", mt.getSet(), DateMatcher.is("2017-07-13T02:08:54Z"));
    }

    @Test
    public void testSequence() {
        long acceptableError = 60 * 1000L;

        Date riseBefore = createDate(2017, 11, 25, 12, 0);
        Date riseAfter = createDate(2017, 11, 26, 12, 29);
        Date setBefore = createDate(2017, 11, 25, 21, 49);
        Date setAfter = createDate(2017, 11, 26, 22, 55);

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute++) {
                MoonTimes times = MoonTimes.compute()
                            .at(COLOGNE)
                            .on(2017, 11, 25, hour, minute, 0).utc()
                            .fullCycle()
                            .execute();

                if (hour < 12 || (hour == 12 && minute == 0)) {
                    long diff = Math.abs(times.getRise().getTime() - riseBefore.getTime());
                    assertThat("rise @" + hour + ":" + minute, diff, is(lessThan(acceptableError)));
                } else {
                    long diff = Math.abs(times.getRise().getTime() - riseAfter.getTime());
                    assertThat("rise @" + hour + ":" + minute, diff, is(lessThan(acceptableError)));
                }

                if (hour < 21 || (hour == 21 && minute <= 49)) {
                    long diff = Math.abs(times.getSet().getTime() - setBefore.getTime());
                    assertThat("set @" + hour + ":" + minute, diff, is(lessThan(acceptableError)));
                } else {
                    long diff = Math.abs(times.getSet().getTime() - setAfter.getTime());
                    assertThat("set @" + hour + ":" + minute, diff, is(lessThan(acceptableError)));
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

}

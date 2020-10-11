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
import java.util.TimeZone;

import org.assertj.core.api.AbstractDateAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shredzone.commons.suncalc.param.TimeResultParameter.Unit;

/**
 * Unit tests for {@link MoonTimes}.
 */
public class MoonTimesTest {

    @BeforeClass
    public static void init() {
        AbstractDateAssert.registerCustomDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    }

    @Test
    public void testCologne() {
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 12).utc().at(COLOGNE)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt.getRise()).as("rise").isEqualTo("2017-07-12T21:25:55Z");
        assertThat(mt.getSet()).as("set").isEqualTo("2017-07-12T06:53:30Z");
        assertThat(mt.isAlwaysUp()).as("alwaysup").isFalse();
        assertThat(mt.isAlwaysDown()).as("alwaysdown").isFalse();
    }

    @Test
    public void testAlert() {
        MoonTimes mt1 = MoonTimes.compute().on(2017, 7, 12).utc().at(ALERT)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt1.isAlwaysUp()).as("alwaysup").isFalse();
        assertThat(mt1.isAlwaysDown()).as("alwaysdown").isTrue();

        MoonTimes mt2 = MoonTimes.compute().on(2017, 7, 12).utc().at(ALERT).fullCycle()
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt2.getRise()).as("rise").isEqualTo("2017-07-14T05:45:05Z");
        assertThat(mt2.getSet()).as("set").isEqualTo("2017-07-14T11:26:43Z");
        assertThat(mt2.isAlwaysUp()).as("alwaysup").isFalse();
        assertThat(mt2.isAlwaysDown()).as("alwaysdown").isTrue();

        MoonTimes mt3 = MoonTimes.compute().on(2017, 7, 14).utc().at(ALERT)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt3.getRise()).as("rise").isEqualTo("2017-07-14T05:45:05Z");
        assertThat(mt3.getSet()).as("set").isEqualTo("2017-07-14T11:26:43Z");
        assertThat(mt3.isAlwaysUp()).as("alwaysup").isFalse();
        assertThat(mt3.isAlwaysDown()).as("alwaysdown").isFalse();

        MoonTimes mt4 = MoonTimes.compute().on(2017, 7, 18).utc().at(ALERT).oneDay()
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt4.isAlwaysUp()).as("alwaysup").isTrue();
        assertThat(mt4.isAlwaysDown()).as("alwaysdown").isFalse();

        MoonTimes mt5 = MoonTimes.compute().on(2017, 7, 18).utc().at(ALERT).fullCycle()
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt5.getRise()).as("rise").isEqualTo("2017-07-27T11:59:20Z");
        assertThat(mt5.getSet()).as("set").isEqualTo("2017-07-27T04:07:10Z");
        assertThat(mt5.isAlwaysUp()).as("alwaysup").isTrue();
        assertThat(mt5.isAlwaysDown()).as("alwaysdown").isFalse();
    }

    @Test
    public void testWellington() {
        MoonTimes mt1 = MoonTimes.compute().on(2017, 7, 12).utc().at(WELLINGTON)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt1.getRise()).as("rise").isEqualTo("2017-07-12T08:05:53Z");
        assertThat(mt1.getSet()).as("set").isEqualTo("2017-07-12T21:57:38Z");

        MoonTimes mt2 = MoonTimes.compute().on(2017, 7, 12).timezone("NZ").at(WELLINGTON)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt2.getRise()).as("rise").isEqualTo("2017-07-12T20:05:53+12:00");
        assertThat(mt2.getSet()).as("set").isEqualTo("2017-07-12T09:23:00+12:00");
    }

    @Test
    public void testPuertoWilliams() {
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 13).utc().at(PUERTO_WILLIAMS)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt.getRise()).as("rise").isEqualTo("2017-07-13T00:31:29Z");
        assertThat(mt.getSet()).as("set").isEqualTo("2017-07-13T14:48:37Z");
    }

    @Test
    public void testSingapore() {
        MoonTimes mt = MoonTimes.compute().on(2017, 7, 13).utc().at(SINGAPORE)
                        .truncatedTo(Unit.SECONDS).execute();
        assertThat(mt.getRise()).as("rise").isEqualTo("2017-07-13T14:35:09Z");
        assertThat(mt.getSet()).as("set").isEqualTo("2017-07-13T02:08:57Z");
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
                            .truncatedTo(Unit.SECONDS)
                            .execute();

                Date rise = times.getRise();
                Date set = times.getSet();

                assertThat(rise).isNotNull();
                assertThat(set).isNotNull();

                if (hour < 12 || (hour == 12 && minute == 0)) {
                    long diff = Math.abs(rise.getTime() - riseBefore.getTime());
                    assertThat(diff).as("rise @%02d:%02d", hour, minute).isLessThan(acceptableError);
                } else {
                    long diff = Math.abs(rise.getTime() - riseAfter.getTime());
                    assertThat(diff).as("rise @%02d:%02d", hour, minute).isLessThan(acceptableError);
                }

                if (hour < 21 || (hour == 21 && minute <= 49)) {
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

}

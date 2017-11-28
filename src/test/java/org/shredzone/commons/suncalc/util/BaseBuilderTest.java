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

import static java.lang.Math.toRadians;
import static org.assertj.core.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.assertj.core.api.AbstractDateAssert;
import org.assertj.core.data.Offset;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link BaseBuilder}.
 *
 * @author Richard "Shred" Körber
 */
public class BaseBuilderTest {

    private static final Offset<Double> ERROR = Offset.offset(0.001);
    private static final Calendar NOW = Calendar.getInstance();

    @BeforeClass
    public static void init() {
        AbstractDateAssert.registerCustomDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    }

    @Test
    public void testLocationParameters() {
        TestBuilder p = new TestBuilder();
        TestBuilder r;

        assertLatLng(p, 0.0, 0.0, 0.0);

        r = p.at(12.34, 34.56);
        assertLatLng(p, 12.34, 34.56, 0.0);
        assertThat(r).isSameAs(p);

        r = p.at(new double[] { 13.43, 51.23 });
        assertLatLng(p, 13.43, 51.23, 0.0);
        assertThat(r).isSameAs(p);

        r = p.latitude(-11.22);
        assertLatLng(p, -11.22, 51.23, 0.0);
        assertThat(r).isSameAs(p);

        r = p.longitude(-8.23);
        assertLatLng(p, -11.22, -8.23, 0.0);
        assertThat(r).isSameAs(p);

        r = p.latitude(5, 7, 37.2);
        assertLatLng(p, 5.127, -8.23, 0.0);
        assertThat(r).isSameAs(p);

        r = p.longitude(-12, 43, 22.8);
        assertLatLng(p, 5.127, -12.723, 0.0);
        assertThat(r).isSameAs(p);

        r = p.height(18267.3);
        assertLatLng(p, 5.127, -12.723, 18267.3);
        assertThat(r).isSameAs(p);

        r = p.at(new double[] { 1.22, -3.44, 323.0 });
        assertLatLng(p, 1.22, -3.44, 323.0);
        assertThat(r).isSameAs(p);
    }

    @Test
    public void testBadLocations() {
        TestBuilder p = new TestBuilder();

        try {
            p.at(new double[] { 12.0 });
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            p.at(new double[] { 12.0, 34.0, 56.0, 78.0 });
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            p.latitude(-90.1);
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            p.latitude(90.1);
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            p.longitude(-180.1);
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        } catch (IllegalArgumentException ex) {
            // expected
        }

        try {
            p.longitude(180.1);
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testTimeParameters() {
        TestBuilder p = new TestBuilder();
        TestBuilder r;

        assertDate(p,
                NOW.get(Calendar.YEAR),
                NOW.get(Calendar.MONTH) + 1,
                NOW.get(Calendar.DAY_OF_MONTH),
                NOW.get(Calendar.HOUR_OF_DAY),
                NOW.get(Calendar.MINUTE),
                NOW.get(Calendar.SECOND),
                NOW.getTimeZone());

        r = p.on(2017, 8, 12);
        assertDate(p, 2017, 8, 12, 0, 0, 0, TimeZone.getDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2012, 3, 11, 8, 1, 12).midnight();
        assertDate(p, 2012, 3, 11, 0, 0, 0, TimeZone.getDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2016, 4, 10, 14, 11, 59);
        assertDate(p, 2016, 4, 10, 14, 11, 59, TimeZone.getDefault());
        assertThat(r).isSameAs(p);

        r = p.timezone("Europe/Berlin");
        assertDate(p, 2016, 4, 10, 14, 11, 59, TimeZone.getTimeZone("Europe/Berlin"));
        assertThat(r).isSameAs(p);

        r = p.timezone(TimeZone.getTimeZone("JST"));
        assertDate(p, 2016, 4, 10, 14, 11, 59, TimeZone.getTimeZone("JST"));
        assertThat(r).isSameAs(p);

        r = p.utc();
        assertDate(p, 2016, 4, 10, 14, 11, 59, TimeZone.getTimeZone("UTC"));
        assertThat(r).isSameAs(p);

        r = p.localTime();
        assertDate(p, 2016, 4, 10, 14, 11, 59, TimeZone.getDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 1, 1, 2, 3, 4).now();
        assertDate(p,
                NOW.get(Calendar.YEAR),
                NOW.get(Calendar.MONTH) + 1,
                NOW.get(Calendar.DAY_OF_MONTH),
                NOW.get(Calendar.HOUR_OF_DAY),
                NOW.get(Calendar.MINUTE),
                NOW.get(Calendar.SECOND),
                NOW.getTimeZone());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 2, 2, 3, 4, 5).today();
        assertDate(p,
                NOW.get(Calendar.YEAR),
                NOW.get(Calendar.MONTH) + 1,
                NOW.get(Calendar.DAY_OF_MONTH),
                0, 0, 0,
                NOW.getTimeZone());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 3, 3, 4, 5, 6).on(NOW);
        assertDate(p,
                NOW.get(Calendar.YEAR),
                NOW.get(Calendar.MONTH) + 1,
                NOW.get(Calendar.DAY_OF_MONTH),
                NOW.get(Calendar.HOUR_OF_DAY),
                NOW.get(Calendar.MINUTE),
                NOW.get(Calendar.SECOND),
                NOW.getTimeZone());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 4, 4, 5, 6, 7).on(new Date(NOW.getTimeInMillis()));
        assertDate(p,
                NOW.get(Calendar.YEAR),
                NOW.get(Calendar.MONTH) + 1,
                NOW.get(Calendar.DAY_OF_MONTH),
                NOW.get(Calendar.HOUR_OF_DAY),
                NOW.get(Calendar.MINUTE),
                NOW.get(Calendar.SECOND),
                NOW.getTimeZone());
        assertThat(r).isSameAs(p);
    }

    private void assertLatLng(TestBuilder p, double lat, double lng, double height) {
        assertThat(p.getLatitude()).as("latitude").isCloseTo(lat, ERROR);
        assertThat(p.getLongitude()).as("longitude").isCloseTo(lng, ERROR);
        assertThat(p.getLatitudeRad()).as("latitude-rad").isCloseTo(toRadians(lat), ERROR);
        assertThat(p.getLongitudeRad()).as("longitude-rad").isCloseTo(toRadians(lng), ERROR);
        assertThat(p.getHeight()).as("height").isCloseTo(height, ERROR);
    }

    private void assertDate(TestBuilder p, int year, int month, int day,
            int hour, int minute, int second, TimeZone tz) {
        Calendar cal = p.getJulianDate().getCalendar();

        assertThat(cal.get(Calendar.YEAR)).as("year").isEqualTo(year);
        assertThat(cal.get(Calendar.MONTH)).as("month").isEqualTo(month - 1);
        assertThat(cal.get(Calendar.DAY_OF_MONTH)).as("day").isEqualTo(day);
        assertThat(cal.get(Calendar.HOUR_OF_DAY)).as("hour").isEqualTo(hour);
        assertThat(cal.get(Calendar.MINUTE)).as("minute").isEqualTo(minute);
        assertThat(cal.get(Calendar.SECOND)).as("second").isEqualTo(second);
        assertThat(cal.getTimeZone()).as("timezone").isEqualTo(tz);
    }

    private static class TestBuilder extends BaseBuilder<TestBuilder> {
        @Override
        protected Calendar createCalendar() {
            return NOW;
        }
    }

}

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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import org.assertj.core.api.AbstractDateAssert;
import org.assertj.core.data.Offset;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shredzone.commons.suncalc.Locations;

/**
 * Unit tests for {@link BaseBuilder}.
 *
 * @author Richard "Shred" Körber
 */
public class BaseBuilderTest {

    private static final Offset<Double> ERROR = Offset.offset(0.001);
    private static final ZonedDateTime NOW = ZonedDateTime.now();

    @BeforeClass
    public static void init() {
        AbstractDateAssert.registerCustomDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    }

    @Test
    public void testLocationParameters() {
        TestBuilder p = new TestBuilder();
        TestBuilder r;

        assertThatIllegalStateException().isThrownBy(p::getLatitude);
        assertThatIllegalStateException().isThrownBy(p::getLongitude);
        assertThatNoException().isThrownBy(p::getElevation);
        assertThat(p.hasLocation()).isFalse();

        p.latitude(0.0);
        assertThatNoException().isThrownBy(p::getLatitude);
        assertThatIllegalStateException().isThrownBy(p::getLongitude);
        assertThat(p.hasLocation()).isFalse();

        p.longitude(0.0);
        assertThatNoException().isThrownBy(p::getLatitude);
        assertThatNoException().isThrownBy(p::getLongitude);
        assertThat(p.hasLocation()).isTrue();

        p.clearLocation();
        assertThatIllegalStateException().isThrownBy(p::getLatitude);
        assertThatIllegalStateException().isThrownBy(p::getLongitude);
        assertThat(p.hasLocation()).isFalse();

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

        r = p.elevation(18267.3);
        assertLatLng(p, 5.127, -12.723, 18267.3);
        assertThat(r).isSameAs(p);

        // Negative elevations are always changed to 0.0
        r = p.elevation(-10.2);
        assertLatLng(p, 5.127, -12.723, 0.0);
        assertThat(r).isSameAs(p);

        r = p.elevationFt(12000.0);
        assertLatLng(p, 5.127, -12.723, 3657.6);
        assertThat(r).isSameAs(p);

        r = p.at(new double[] { 1.22, -3.44, 323.0 });
        assertLatLng(p, 1.22, -3.44, 323.0);
        assertThat(r).isSameAs(p);

        TestBuilder s = new TestBuilder();
        s.sameLocationAs(p);
        assertLatLng(s, 1.22, -3.44, 323.0);
    }

    @Test
    public void testBadLocations() {
        TestBuilder p = new TestBuilder();

        // At least two array records are required
        assertThatIllegalArgumentException()
                .isThrownBy(() -> p.at(new double[] { 12.0 }));

        // No more than three array records are permitted
        assertThatIllegalArgumentException()
                .isThrownBy(() -> p.at(new double[] { 12.0, 34.0, 56.0, 78.0 }));

        // Latitude out of range (negative)
        assertThatIllegalArgumentException()
                .isThrownBy(() -> p.latitude(-90.1));

        // Latitude out of range (positive)
        assertThatIllegalArgumentException()
                .isThrownBy(() -> p.latitude(90.1));

        // Longitude out of range (negative)
        assertThatIllegalArgumentException()
                .isThrownBy(() -> p.longitude(-180.1));

        // Longitude out of range (positive)
        assertThatIllegalArgumentException()
                .isThrownBy(() -> p.longitude(180.1));
    }

    @Test
    public void testTimeParameters() {
        TestBuilder p = new TestBuilder();
        TestBuilder r;

        assertThat(p.getJulianDate().getDateTime()).isNotNull();

        p.on(NOW);
        assertDate(p,
                NOW.getYear(),
                NOW.getMonthValue(),
                NOW.getDayOfMonth(),
                NOW.getHour(),
                NOW.getMinute(),
                NOW.getSecond(),
                NOW.getZone());

        r = p.on(2017, 8, 12);
        assertDate(p, 2017, 8, 12, 0, 0, 0, ZoneId.systemDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2016, 4, 10, 14, 11, 59);
        assertDate(p, 2016, 4, 10, 14, 11, 59, ZoneId.systemDefault());
        assertThat(r).isSameAs(p);

        r = p.timezone("Europe/Berlin");
        assertDate(p, 2016, 4, 10, 14, 11, 59, ZoneId.of("Europe/Berlin"));
        assertThat(r).isSameAs(p);

        r = p.timezone(ZoneId.of("Asia/Tokyo"));
        assertDate(p, 2016, 4, 10, 14, 11, 59, ZoneId.of("Asia/Tokyo"));
        assertThat(r).isSameAs(p);

        r = p.timezone(TimeZone.getTimeZone("America/New_York"));
        assertDate(p, 2016, 4, 10, 14, 11, 59, ZoneId.of("America/New_York"));
        assertThat(r).isSameAs(p);

        r = p.utc();
        assertDate(p, 2016, 4, 10, 14, 11, 59, ZoneId.of("UTC"));
        assertThat(r).isSameAs(p);

        r = p.on(LocalDate.of(2020, 3, 12));
        assertDate(p, 2020, 3, 12, 0, 0, 0, ZoneId.of("UTC"));
        assertThat(r).isSameAs(p);

        r = p.on(ZonedDateTime.of(2020, 7, 11, 2, 44, 12, 0, ZoneId.of("UTC")).toInstant());
        assertDate(p, 2020, 7, 11, 2, 44, 12, ZoneId.of("UTC"));
        assertThat(r).isSameAs(p);

        r = p.on(LocalDateTime.of(2020, 4, 1, 12, 45, 33));
        assertDate(p, 2020, 4, 1, 12, 45, 33, ZoneId.of("UTC"));
        assertThat(r).isSameAs(p);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        cal.set(2018, Calendar.AUGUST, 12, 3, 23, 11);
        r = p.on(cal);
        assertDate(p, 2018, 8, 12, 3, 23, 11, ZoneId.of("Europe/Berlin"));
        assertThat(r).isSameAs(p);

        cal.set(Calendar.YEAR, 2019);
        r = p.on(cal.getTime());
        assertDate(p, 2019, 8, 12, 3, 23, 11, ZoneId.of("Europe/Berlin"));
        assertThat(r).isSameAs(p);

        r = p.localTime();
        assertDate(p, 2019, 8, 12, 3, 23, 11, ZoneId.systemDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2012, 3, 11, 8, 1, 12).midnight();
        assertDate(p, 2012, 3, 11, 0, 0, 0, ZoneId.systemDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2012, 1, 24, 1, 33, 12).plusDays(100);
        assertDate(p, 2012, 5, 3, 1, 33, 12, ZoneId.systemDefault());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 1, 1, 2, 3, 4).now();
        assertDate(p,
                NOW.getYear(),
                NOW.getMonthValue(),
                NOW.getDayOfMonth(),
                NOW.getHour(),
                NOW.getMinute(),
                NOW.getSecond(),
                NOW.getZone());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 2, 2, 3, 4, 5).today();
        assertDate(p,
                NOW.getYear(),
                NOW.getMonthValue(),
                NOW.getDayOfMonth(),
                0, 0, 0,
                NOW.getZone());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 2, 2, 3, 4, 5).tomorrow();
        ZonedDateTime tomorrow = NOW.plusDays(1);
        assertDate(p,
                tomorrow.getYear(),
                tomorrow.getMonthValue(),
                tomorrow.getDayOfMonth(),
                0, 0, 0,
                tomorrow.getZone());
        assertThat(r).isSameAs(p);

        r = p.on(2000, 3, 3, 4, 5, 6).on(NOW);
        assertDate(p,
                NOW.getYear(),
                NOW.getMonthValue(),
                NOW.getDayOfMonth(),
                NOW.getHour(),
                NOW.getMinute(),
                NOW.getSecond(),
                NOW.getZone());
        assertThat(r).isSameAs(p);

        TestBuilder s = new TestBuilder();
        s.sameTimeAs(p);
        assertDate(s,
                NOW.getYear(),
                NOW.getMonthValue(),
                NOW.getDayOfMonth(),
                NOW.getHour(),
                NOW.getMinute(),
                NOW.getSecond(),
                NOW.getZone());
    }

    @Test
    public void testWindowParameters() {
        TestBuilder p = new TestBuilder();
        TestBuilder r;

        assertThat(p.getDuration()).isNotNull().isEqualTo(Duration.ofDays(365L));

        r = p.oneDay();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(1L));
        assertThat(r).isSameAs(p);

        r = p.fullCycle();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(365L));
        assertThat(r).isSameAs(p);

        r = p.limit(Duration.ofDays(14L).negated());
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(14L).negated());
        assertThat(r).isSameAs(p);

        r = p.forward();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(14L));
        assertThat(r).isSameAs(p);

        // Second forward won't negate again!
        r = p.forward();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(14L));
        assertThat(r).isSameAs(p);

        r = p.reverse();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(14L).negated());
        assertThat(r).isSameAs(p);

        // Second reverse won't negate again!
        r = p.reverse();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(14L).negated());
        assertThat(r).isSameAs(p);

        r = p.forward();
        assertThat(p.getDuration()).isEqualTo(Duration.ofDays(14L));
        assertThat(r).isSameAs(p);

        r = p.limit(Duration.ofHours(12L));
        assertThat(p.getDuration()).isEqualTo(Duration.ofHours(12L));
        assertThat(r).isSameAs(p);

        TestBuilder s = new TestBuilder();
        s.sameWindowAs(p);
        assertThat(s.getDuration()).isEqualTo(Duration.ofHours(12L));

        p.reverse();
        TestBuilder s2 = new TestBuilder();
        s2.sameWindowAs(p);
        assertThat(s2.getDuration()).isEqualTo(Duration.ofHours(12L).negated());

        assertThatNullPointerException().isThrownBy(() -> {
            p.limit(null);
        });
    }

    @Test
    public void testCopy() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrow = now.plusDays(1);
        ZonedDateTime yesterday = now.minusDays(1);
        Duration duration = Duration.ofDays(60L);

        // Set test parameters
        TestBuilder p1 = new TestBuilder();
        p1.at(Locations.COLOGNE);
        p1.on(now);
        p1.elevation(123.0);
        p1.limit(duration);

        // Make sure copy has identical values
        TestBuilder p2 = p1.copy();
        assertThat(p2.getLatitude()).isEqualTo(Locations.COLOGNE[0]);
        assertThat(p2.getLongitude()).isEqualTo(Locations.COLOGNE[1]);
        assertThat(p2.getJulianDate().getDateTime()).isEqualTo(now);
        assertThat(p2.getElevation()).isEqualTo(123.0);
        assertThat(p2.getDuration()).isEqualTo(Duration.ofDays(60L));

        // Make sure changes to p1 won't affect p2
        p1.at(Locations.SINGAPORE);
        p1.on(tomorrow);
        p1.oneDay();
        assertThat(p1.getLatitude()).isEqualTo(Locations.SINGAPORE[0]);
        assertThat(p1.getLongitude()).isEqualTo(Locations.SINGAPORE[1]);
        assertThat(p1.getJulianDate().getDateTime()).isEqualTo(tomorrow);
        assertThat(p1.getDuration()).isEqualTo(Duration.ofDays(1L));
        assertThat(p2.getLatitude()).isEqualTo(Locations.COLOGNE[0]);
        assertThat(p2.getLongitude()).isEqualTo(Locations.COLOGNE[1]);
        assertThat(p2.getJulianDate().getDateTime()).isEqualTo(now);
        assertThat(p2.getDuration()).isEqualTo(Duration.ofDays(60L));

        // Make sure changes to p2 won't affect p1
        p2.at(Locations.WELLINGTON);
        p2.on(yesterday);
        p2.fullCycle();
        assertThat(p1.getLatitude()).isEqualTo(Locations.SINGAPORE[0]);
        assertThat(p1.getLongitude()).isEqualTo(Locations.SINGAPORE[1]);
        assertThat(p1.getJulianDate().getDateTime()).isEqualTo(tomorrow);
        assertThat(p1.getDuration()).isEqualTo(Duration.ofDays(1L));
        assertThat(p2.getLatitude()).isEqualTo(Locations.WELLINGTON[0]);
        assertThat(p2.getLongitude()).isEqualTo(Locations.WELLINGTON[1]);
        assertThat(p2.getJulianDate().getDateTime()).isEqualTo(yesterday);
        assertThat(p2.getDuration()).isEqualTo(Duration.ofDays(365L));
    }

    private void assertLatLng(TestBuilder p, double lat, double lng, double elev) {
        assertThat(p.getLatitude()).as("latitude").isCloseTo(lat, ERROR);
        assertThat(p.getLongitude()).as("longitude").isCloseTo(lng, ERROR);
        assertThat(p.getLatitudeRad()).as("latitude-rad").isCloseTo(toRadians(lat), ERROR);
        assertThat(p.getLongitudeRad()).as("longitude-rad").isCloseTo(toRadians(lng), ERROR);
        assertThat(p.getElevation()).as("elevation").isCloseTo(elev, ERROR);
    }

    private void assertDate(TestBuilder p, int year, int month, int day,
            int hour, int minute, int second, ZoneId tz) {
        ZonedDateTime cal = p.getJulianDate().getDateTime();

        assertThat(cal.getYear()).as("year").isEqualTo(year);
        assertThat(cal.getMonthValue()).as("month").isEqualTo(month);
        assertThat(cal.getDayOfMonth()).as("day").isEqualTo(day);
        assertThat(cal.getHour()).as("hour").isEqualTo(hour);
        assertThat(cal.getMinute()).as("minute").isEqualTo(minute);
        assertThat(cal.getSecond()).as("second").isEqualTo(second);
        assertThat(cal.getZone()).as("timezone").isEqualTo(tz);
    }

    private static class TestBuilder extends BaseBuilder<TestBuilder> {
        @Override
        public TestBuilder now() {
            return on(NOW);
        }
    }

}

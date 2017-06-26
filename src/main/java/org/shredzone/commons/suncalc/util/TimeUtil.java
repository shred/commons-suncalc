package org.shredzone.commons.suncalc.util;

import static java.lang.Math.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {
    public static double hourToDays(double h) {
        return h / 24.0;
    }

    public static double millisToDays(double m) {
        return m / 86400000.0;
    }

    public static double dateToMJD(Date javaDate, long zoneOffset) {
        final long localTime = javaDate.getTime() + zoneOffset;
        final double localMJD = ((double) localTime) / 86400000 + 40587;

        return (floor(localMJD) - millisToDays(zoneOffset));
    }

    public static Date doubleToDate(Double time, Date date, TimeZone tz) {
        if (time == null)
            return null;

        int hours = (int) Math.floor(time);

        int minutes = new BigDecimal((time - hours) * 60.0).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();

        Calendar cal = new GregorianCalendar(tz);
        cal.setTime(new Date(date.getTime()));
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        return cal.getTime();
    }
}

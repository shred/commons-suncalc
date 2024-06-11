# Usage

`commons-suncalc` offers six astronomical calculations:

* [SunTimes](./apidocs/org/shredzone/commons/suncalc/SunTimes.html): Sunrise, sunset, noon and nadir times.
* [MoonTimes](./apidocs/org/shredzone/commons/suncalc/MoonTimes.html): Moonrise and moonset times.
* [MoonPhase](./apidocs/org/shredzone/commons/suncalc/MoonPhase.html): Date and time of new moon, full moon and half moons.
* [SunPosition](./apidocs/org/shredzone/commons/suncalc/SunPosition.html): Position of the sun.
* [MoonPosition](./apidocs/org/shredzone/commons/suncalc/MoonPosition.html): Position of the moon.
* [MoonIllumination](./apidocs/org/shredzone/commons/suncalc/MoonIllumination.html): Moon phase and angle.

## Quick Start

All of the calculations mentioned above are invoked in the same way:

```java
ZonedDateTime dateTime =    // date, time and timezone of calculation
double lat, lng =           // geolocation
SunTimes times = SunTimes.compute()
            .on(dateTime)   // set a date
            .at(lat, lng)   // set a location
            .execute();     // get the results
System.out.println("Sunrise: " + times.getRise());
System.out.println("Sunset: " + times.getSet());
```

You invoke `compute()`, set your parameters, invoke `execute()`, and then get the result of the calculation as an object.

All parameters are passed in by a fluent builder-style interface. After retrieving the builder from `compute()`, you can chain the parameter setters, and finally call `execute()` to perform the computation.

```java
SunPosition pos = SunPosition.compute().today().at(12.3, 45.6).execute();
```

It is also possible to collect the parameters first, and execute the computation in a separate step:

```java
SunPosition.Parameters param = SunPosition.compute();
param.today();
param.at(12.3, 45.6);

SunPosition pos = param.execute();
```

The instance that is returned by `execute()` is immutable and only holds the calculation result of the current set of parameters. You can modify the parameters without changing the first result, then call `execute()` again for a second result.

```java
param.on(2016, 12, 24); // modify the param from above

SunPosition posAtChristmas = param.execute();
// pos from above is unchanged
```

## Time-based Parameters

All calculations need a date and time parameter. Some examples:

```java
// Now (the default)
SunPosition.compute().now();

// The same: Current time, local time zone
ZonedDateTime now = ZonedDateTime.now();
SunPosition.compute().on(now);

// August 21st, 2017, local midnight
SunPosition.compute().on(2017, 8, 21);

// Today (midnight), Berlin time zone
SunPosition.compute().today().timezone("Europe/Berlin");
```

The available time-based parameters are:

* `on(int year, int month, int date)`: Midnight of the given date. Note that `month` is counted from 1 (1 = January, 2 = February, …).
* `on(int year, int month, int date, int hour, int minute, int second)`: Given date and time.
* `on(ZonedDateTime dateTime)`: Given date, time, and timezone.
* `on(LocalDateTime dateTime)`: Given local date and time, without a timezone.
* `on(LocalDate date)`: Midnight of the given local date, without a timezone.
* `on(Instant instant)`: An instant without a timezone.
* `on(Calendar cal)`: Date, time and timezone as given in the old-fashioned `Calendar`. The `Calender` is copied and can safely be modified after that.
* `on(Date date)`: Date and time as given in the old-fashioned `Date`. The `Date` is copied and can safely be modified after that.
* `plusDays(int days)`: Adds the given number of days to the current date. `days` can also be negative, of course.
* `now()`: The current system date and time. This is the default.
* `midnight()`: Past midnight of the current date. It just truncates the time.
* `today()`: Identical to `.now().midnight()`.
* `tomorrow()`: Identical to `today().plusDays(1)`.
* `timezone(ZoneId tz)`: Use the given `ZoneId` as timezone. The current local time is unchanged (this is, it is not converted to the new timezone), so the order of parameters is not important.
* `timezone(String id)`: Same as above, but accepts a `String` for your convenience.
* `timezone(TimeZone tz)`: Same as above, but accepts an old-fashioned `TimeZone` object.
* `localTime()`: The system's timezone. This is the default.
* `utc()`: UTC timezone. Identical to `timezone("UTC")`.
* `sameTimeAs(TimeParameter<?> t)`: Copies the current date, time, and timezone from any other parameter object. Note that subsequent changes to the other object are not adopted.

If no time-based parameter is given, the current date and time, and the system's time zone is used.

!!! NOTE
    The accuracy of the results is decreasing for dates that are far in the future, or far in the past.

## Location-based Parameters

The geolocation is required, and `execute()` will throw an exception if the latitude or longitude is missing. The elevation is optional, and is 0 meters (sea level) if not set.

```java
// At 20.5°N, 18.3°E
SunPosition.compute().at(20.5, 18.3);

// The same, but more verbose
SunPosition.compute().latitude(20.5).longitude(18.3);

// Use arrays for coordinate constants
final double[] COLOGNE = new double[] { 50.938056, 6.956944 };
SunPosition.compute().at(COLOGNE);
```

There are two exceptions:

* [`MoonIllumination`](./apidocs/org/shredzone/commons/suncalc/MoonIllumination.Parameters.html):  If the geolocation is set, the result is topocentric. If the geolocation is unset, the result is geocentric.
* [`MoonPhase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Parameters.html): The geolocation is not used here.

The available location-based parameters are:

* `at(double lat, double lng)`: Latitude and longitude to be used for computation.
* `at(double[] coords)`: Accepts an array of 2 values (latitude, longitude) or 3 values (latitude, longitude, elevation).
* `latitude(double lat)`: Verbose way to set the latitude only.
* `longitude(double lng)`: Verbose way to set the longitude only.
* `latitude(int d, int m, double s)`: Set the latitude in degrees, minutes, seconds and fraction of seconds.
* `longitude(int d, int m, double s)`: Set the longitude in degrees, minutes, seconds and fraction of seconds.
* `elevation(double h)`: Elevation above sea level, in meters. Sea level is used by default.
* `elevationFt(double h)`: Elevation above sea level, in foot. Sea level is used by default.
* `sameLocationAs(LocationParameter<?> l)`: Copies the current location and elevation from any other parameter object. Note that subsequent changes to the other object are not adoped.

!!! NOTE
    `elevation` cannot be negative. If you pass in a negative elevation, it is silently changed to the accepted minimum of 0 meters. For this reason, it is safe to pass coordinates from satellite-based navigation systems without range checking.

## Window-based Parameters

By default, [`SunTimes`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html) and [`MoonTimes`](./apidocs/org/shredzone/commons/suncalc/MoonTimes.Parameters.html) will calculate a full cycle of the sun or moon. This means that rise, set, noon, and nadir times may be more than 24 hours ahead. You can limit this time window.

The available window-based parameters are:

* `limit(Duration duration)`: Limits the time window to the given duration. A reverse time window is implicitly set if this value is negative.
* `oneDay()`: Limits the time window to 24 hours.
* `fullCycle()`: No limit. Calculation will find all rise, set, noon, and nadir times.
* `reverse()`: Sets a reverse time window. It will end at the given time. The rise, set, noon, and nadir times will be in the past. You can also pass a negative duration as `limit()`.
* `forward()`: Sets a forward time window. It will start at the given time. The rise, set, noon, and nadir times will be in the future. This is the default.
* `sameWindowAs(WindowParameter<?> w)`: Copies the current time window from any other parameter object. Note that subsequent changes to the other object are not adoped.

If the sun or moon does not rise or set within the given window, the appropriate getters return `null`. You can check if the sun or moon is always above or below the horizon, by checking `isAlwaysUp()` and `isAlwaysDown()`.

## Twilight

<img src="twilights.svg" alt="Twilight Zones" align="right" width="550px"/>

By default, [`SunTimes`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html) calculates the time of the visual sunrise and sunset. This means that `getRise()` returns the instant when the sun just starts to rise above the horizon, and `getSet()` returns the instant when the sun just disappeared from the horizon. [Atmospheric refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction) is taken into account.

There are other interesting [twilight](https://en.wikipedia.org/wiki/Twilight) angles available. You can set them via the `twilight()` parameter, by using one of the [`SunTimes.Twilight`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Twilight.html) constants:

| Constant       | Description | Angle of the Sun | Topocentric |
| -------------- | ----------- | ----------------:|:-----------:|
| `VISUAL`       | The moment when the visual upper edge of the sun crosses the horizon. This is the default. | | yes |
| `VISUAL_LOWER` | The moment when the visual lower edge of the sun crosses the horizon. | | yes |
| `ASTRONOMICAL` | [Astronomical twilight](https://en.wikipedia.org/wiki/Twilight#Astronomical_twilight) | -18° | no |
| `NAUTICAL`     | [Nautical twilight](https://en.wikipedia.org/wiki/Twilight#Nautical_twilight) | -12° | no |
| `CIVIL`        | [Civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight) | -6° | no |
| `HORIZON`      | The moment when the center of the sun crosses the horizon. | 0° | no |
| `GOLDEN_HOUR`  | Transition from daylight to [Golden Hour](https://en.wikipedia.org/wiki/Golden_hour_%28photography%29) | 6° | no |
| `BLUE_HOUR`    | Transition from [Golden Hour](https://en.wikipedia.org/wiki/Golden_hour_%28photography%29) to [Blue Hour](https://en.wikipedia.org/wiki/Blue_hour) | -4° | no |
| `NIGHT_HOUR`   | Transition from [Blue Hour](https://en.wikipedia.org/wiki/Blue_hour) to night | -8° | no |

The illustration shows the transitions of each twilight constant. If you want to get the duration of a twilight, you need to calculate the times of both transitions of the twilight. For example, to get the beginning and ending of the civil twilight, you need to calculate both the `VISUAL` and the `CIVIL` twilight transition times.

Alternatively you can also pass any other angle (in degrees) to `twilight()`.

!!! NOTE
    Only `VISUAL` and `VISUAL_LOWER` are topocentric. They refer to the visual edge of the sun, take account of the `elevation` parameter, and compensate atmospheric refraction.
    
    All other twilights are geocentric and heliocentric. The `elevation` parameter is then ignored, and atmospheric refraction is not compensated.

Example:

```java
SunTimes.compute().twilight(SunTimes.Twilight.GOLDEN_HOUR);
```

## Phase

By default, [`MoonPhase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Parameters.html) calculates the date of the next new moon. If you want to compute the date of another phase, you can set it via the `phase()` parameter, by using one of the [`MoonPhase.Phase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Phase.html) constants:

| Constant          | Description | Angle |
| ----------------- | ----------- | -----:|
| `NEW_MOON`        | Moon is not illuminated (new moon). This is the default. | 0° |
| `WAXING_CRESCENT` | Waxing crescent moon. | 45° |
| `FIRST_QUARTER`   | Half of the waxing moon is illuminated. | 90° |
| `WAXING_GIBBOUS`  | Waxing gibbous moon. | 135° |
| `FULL_MOON`       | Moon is fully illuminated. | 180° |
| `WANING_GIBBOUS`  | Waning gibbous moon. | 225° |
| `LAST_QUARTER`    | Half of the waning moon is illuminated. | 270° |
| `WANING_CRESCENT` | Waning crescent moon. | 315° |

Alternatively you can also pass any other angle (in degrees) to `phase()`.

Example:

```java
MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON);
```

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
Date date = // date of calculation
double lat, lng = // geolocation
SunTimes times = SunTimes.compute()
            .on(date)       // set a date
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
// August 21st, 2017, local midnight
SunPosition.compute().on(2017, 8, 21);

// Current time, system time zone
Date now = new Date();
SunPosition.compute().on(now);

// Current time, UTC
Date now = new Date();
SunPosition.compute().on(now).utc();

// Now (the default)
SunPosition.compute().now();

// Today (midnight), Berlin time zone
SunPosition.compute().today().timezone("Europe/Berlin");
```

The available time-based parameters are:

* `on(int year, int month, int date)`: Midnight of the given date. Note that `month` is counted from 1 (1 = January, 2 = February, …).
* `on(int year, int month, int date, int hour, int minute, int second)`: Given date and time.
* `on(Calendar cal)`: Date, time and timezone as given in the `Calendar`. The `Calender` is copied and can safely be modified after that.
* `on(Date date)`: Date and time as given in the `Date`.
* `plusDays(int days)`: Adds the given number of days to the current date. `days` can also be negative, of course.
* `now()`: The current system date and time. This is the default.
* `midnight()`: Past midnight of the current date. It just truncates the time.
* `today()`: Identical to `.now().midnight()`.
* `tomorrow()`: Identical to `today().plusDays(1)`.
* `timezone(TimeZone tz)`: Use the given timezone.
* `timezone(String id)`: Same as above, but accepts a `String` for your convenience.
* `localTime()`: The system's timezone. This is the default.
* `utc()`: UTC timezone. Identical to `timezone("UTC")`.
* `sameTimeAs(TimeParameter<?> t)`: Copies the current date, time, and timezone from any other parameter object. Note that subsequent changes to the other object are not adopted.

If no time-based parameter is given, the current date and time, and the system's time zone is used.

!!! NOTE
    The accuracy of the results is decreasing for dates that are far in the future, or far in the past.

## Location-based Parameters

Except of [`MoonPhase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Parameters.html) and [`MoonIllumination`](./apidocs/org/shredzone/commons/suncalc/MoonIllumination.Parameters.html), all calculations require a geolocation as parameter. Some examples:

```java
// At 20.5°N, 18.3°E
SunPosition.compute().at(20.5, 18.3);

// The same, but more verbose
SunPosition.compute().latitude(20.5).longitude(18.3);

// Use arrays for coordinate constants
final double[] COLOGNE = new double[] { 50.938056, 6.956944 };
SunPosition.compute().at(COLOGNE);
```

The available location-based parameters are:

* `at(double lat, double lng)`: Latitude and longitude to be used for computation.
* `at(double[] coords)`: Accepts an array of 2 values (latitude, longitude) or 3 values (latitude, longitude, height).
* `latitude(double lat)`: Verbose way to set the latitude only.
* `longitude(double lng)`: Verbose way to set the longitude only.
* `latitude(int d, int m, double s)`: Set the latitude in degrees, minutes, seconds and fraction of seconds.
* `longitude(int d, int m, double s)`: Set the longitude in degrees, minutes, seconds and fraction of seconds.
* `height(double h)`: Height above sea level, in meters. Sea level is used by default.
* `sameLocationAs(LocationParameter<?> l)`: Copies the current location and height from any other parameter object. Note that subsequent changes to the other object are not adoped.

!!! WARNING
    The location parameters are not mandatory. However, if they are not given, 0° is assumed as latitude and longitude, which is not very useful in most cases. Do not forget to set the parameters!

!!! NOTE
    `height` cannot be negative. If you pass in a negative height, it is silently changed to the accepted minimum of 0 meters. For this reason, it is safe to pass coordinates from satellite-based navigation systems without range checking.

## Time Range

By default, [`SunTimes`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html) and [`MoonTimes`](./apidocs/org/shredzone/commons/suncalc/MoonTimes.Parameters.html) only consider the next 24 hours of the given start time. If the sun or moon does not rise or set within that time span, the appropriate getters return `null`. You can check if the sun or moon is always above or below the horizon, by checking `isAlwaysUp()` and `isAlwaysDown()`.

If you need both the rise and set time, you can set the `fullCycle()` parameter. The calculation then runs until both times are found, even if several days in the future. However, depending on the date and geolocation, this calculation could take considerably more time and computing power.

!!! NOTE
    `fullCycle()` only affects the result of `getRise()` and `getSet()`. The methods `isAlwaysUp()`, `isAlwaysDown()`, `getNoon()` and `getNadir()` will always only consider the next 24 hours.

## Result Rounding

[`SunTimes`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html), [`MoonTimes`](./apidocs/org/shredzone/commons/suncalc/MoonTimes.Parameters.html) and [`MoonPhase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Parameters.html) return `Date` objects as result. By default, the result is rounded to the nearest full minute. This is so _suncalc_ does not pretend a higher precision than it can actually deliver.

You can change rounding by adding a `truncateTo()` parameter. It accepts one of these constants:

| Constant  | Description |
| --------- | ----------- |
| `SECONDS` | Include the calculated seconds. Note that due to the simplified formulas, _suncalc_ is never accurate to the second. |
| `MINUTES` | Round to the nearest full minute. This is the default. |
| `HOURS`   | Round to the nearest full hour. |
| `DAYS`    | Round *down* to the date. Basically it truncates the time component of the result. |

!!! NOTE
    Even though the method is called `truncateTo()`, the time component is rounded to the nearest full minute or hour. This gives more reasonable results.

## Twilight

By default, [`SunTimes`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html) calculates the time of the visual sunrise and sunset. This means that `getRise()` returns the instant when the sun just starts to rise above the horizon, and `getSet()` returns the instant when the sun just disappeared from the horizon. [Atmospheric refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction) is taken into account.

There are other interesting [twilight](https://en.wikipedia.org/wiki/Twilight) angles available. You can set them via the `twilight()` parameter, by using one of the [`SunTimes.Twilight`](./apidocs/org/shredzone/commons/suncalc/SunTimes.Twilight.html) constants:

| Constant       | Description | Angle of the Sun |
| -------------- | ----------- | ----------------:|
| `ASTRONOMICAL` | [Astronomical twilight](https://en.wikipedia.org/wiki/Twilight#Astronomical_twilight) | -18° |
| `NAUTICAL`     | [Nautical twilight](https://en.wikipedia.org/wiki/Twilight#Nautical_twilight) | -12° |
| `CIVIL`        | [Civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight) | -6° |
| `HORIZON`      | The moment when the center of the sun crosses the horizon. | 0° |
| `GOLDEN_HOUR`  | [Golden Hour](https://en.wikipedia.org/wiki/Golden_hour_%28photography%29) | 6° |
| `BLUE_HOUR`    | [Blue Hour](https://en.wikipedia.org/wiki/Blue_hour) | -4° |
| `VISUAL`       | The moment when the visual upper edge of the sun crosses the horizon. This is the default. | |
| `VISUAL_LOWER` | The moment when the visual lower edge of the sun crosses the horizon. | |

Alternatively you can also pass any other angle (in degrees) to `twilight()`.

!!! NOTE
    Only `VISUAL` and `VISUAL_LOWER` refer to the visual edge of the sun, and compensate atmospheric refraction. All other twilight angles refer to the center of the sun, and ignore atmospheric refraction.

Example:

```java
SunTimes.compute().twilight(SunTimes.Twilight.GOLDEN_HOUR);
```

## Phase

By default, [`MoonPhase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Parameters.html) calculates the date of the next new moon. If you want to compute the date of another phase, you can set it via the `phase()` parameter, by using one of the [`MoonPhase.Phase`](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Phase.html) constants:

| Constant        | Description | Angle |
| --------------- | ----------- | -----:|
| `NEW_MOON`      | Moon is not illuminated (new moon) | 0° |
| `FIRST_QUARTER` | Half of the waxing moon is illuminated | 90° |
| `FULL_MOON`     | Moon is fully illuminated | 180° |
| `LAST_QUARTER`  | Half of the waning moon is illuminated | 270° |

Alternatively you can also pass any other angle (in degrees) to `phase()`.

Example:

```java
MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON);
```

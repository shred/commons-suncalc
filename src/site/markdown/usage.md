# Usage

`commons-suncalc` offers six astronomical calculations:

* [SunTimes](./apidocs/org/shredzone/commons/suncalc/SunTimes.html): Sunrise, sunset, noon and nadir times.
* [MoonTimes](./apidocs/org/shredzone/commons/suncalc/MoonTimes.html): Moonrise and moonset times.
* [MoonPhase](./apidocs/org/shredzone/commons/suncalc/MoonPhase.html): Date and time of new moon, full moon and half moons.
* [SunPosition](./apidocs/org/shredzone/commons/suncalc/SunPosition.html): Position of the sun.
* [MoonPosition](./apidocs/org/shredzone/commons/suncalc/MoonPosition.html): Position of the moon.
* [MoonIllumination](./apidocs/org/shredzone/commons/suncalc/MoonIllumination.html): Moon phase and angle.

## Parameters

All parameters are passed in a fluent builder-style interface. After retrieving the builder from `compute()`, you can chain the parameter setters, and finally call `execute()` to perform the computation.

```java
SunPosition pos = SunPosition.compute().today().at(12.3, 45.6).execute();
```

It is also possible to collect the parameters first, and execute the computation in a separate step:

```java
SunPosition.Parameters param = SunPosition.compute();
param.at(12.3, 45.6);
param.today();

SunPosition pos = param.execute();
```

The instance that is returned by `execute()` is immutable and only holds the result. You can continue modifying the parameters without changing the first result, then call `execute()` again for a second result.

```java
param.at(2016, 12, 24);
SunPosition christmas = param.execute();
// pos from above is unchanged
```

### Time-based Parameters

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

All available setters are listed in the [JavaDocs](./apidocs/org/shredzone/commons/suncalc/param/TimeParameter.html). If no time-based parameter is given, the current date and time, and the system's time zone is used.

> **NOTE:** The accuracy of the results decreases for dates that are far in the future, or far in the past.

### Location-based Parameters

Most of the calculations also need a geolocation. Some examples:

```java
// At 20.5°N, 18.3°E
SunPosition.compute().at(20.5, 18.3);

// The same, but more verbose
SunPosition.compute().latitude(20.5).longitude(18.3);

// Use arrays for coordinate constants
final double[] COLOGNE = new double[] { 50.938056, 6.956944 };
SunPosition.compute().at(COLOGNE);
```

All available setters are listed in the [JavaDocs](./apidocs/org/shredzone/commons/suncalc/param/LocationParameter.html). If no location-based parameter is given, 0° is used for both latitude and longitude, which is not very useful in most cases. However, these parameters are not mandatory.

### Time Range

[SunTimes](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html) and [MoonTimes](./apidocs/org/shredzone/commons/suncalc/MoonTimes.Parameters.html) only consider the next 24 hours. If the sun or moon does not rise or set within that time span, the appropriate getters return `null`. You can check if the sun or moon is always above or below the horizon, by checking `isAlwaysUp()` and `isAlwaysDown()`.

If you need both the rise and set time, you can set the `fullCycle()` parameter. The calculation then runs until both times are found, even if several days in the future. However, depending on the date and geolocation, this calculation could take considerably more time and computing power.

Note that `fullCycle()` only affects the result of `getRise()` and `getSet()`. The methods `isAlwaysUp()`, `isAlwaysDown()`, `getNoon()` and `getNadir()` will still only consider the next 24 hours.

### Twilight

By default, [SunTimes](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html) calculates the time of the visual sunrise and sunset. This means that `getRise()` returns the instant when the sun just starts to rise above the horizon, and `getSet()` returns the instant when the sun just disappeared from the horizon. [Atmospheric refraction](https://en.wikipedia.org/wiki/Atmospheric_refraction) is taken into account.

There are other interesting [twilight](https://en.wikipedia.org/wiki/Twilight) angles available. You can set them via the `twilight()` parameter, by using one of the [SunTimes.Twilight](./apidocs/org/shredzone/commons/suncalc/SunTimes.Twilight.html) constants:

| Constant       | Description | Angle |
| -------------- | --- | ---:|
| `ASTRONOMICAL` | [Astronomical twilight](https://en.wikipedia.org/wiki/Twilight#Astronomical_twilight) | -18° |
| `NAUTICAL`     | [Nautical twilight](https://en.wikipedia.org/wiki/Twilight#Nautical_twilight) | -12° |
| `CIVIL`        | [Civil twilight](https://en.wikipedia.org/wiki/Twilight#Civil_twilight) | -6° |
| `HORIZON`      | The true moment when the center of the sun crosses the horizon. | 0° |
| `GOLDEN_HOUR`  | [Golden Hour](https://en.wikipedia.org/wiki/Golden_hour_%28photography%29) | 6° |
| `BLUE_HOUR`    | [Blue Hour](https://en.wikipedia.org/wiki/Blue_hour) | -4° |
| `VISUAL`       | **Default:** The moment when the visual upper edge of the sun crosses the horizon. | |
| `VISUAL_LOWER` | The moment when the visual lower edge of the sun crosses the horizon. | |

Alternatively you can also pass any other angle (in degrees) to `twilight()`.

Example:

```java
SunTimes.compute().twilight(SunTimes.Twilight.GOLDEN_HOUR);
```

Note that only `VISUAL` and `VISUAL_LOWER` compensate atmospheric refraction. All other twilights do not, and refer to the center of the sun.

### Phase

By default, [MoonPhase](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Parameters.html) calculates the date of the next new moon. If you want to compute the date of another phase, you can set it via the `phase()` parameter, by using one of the [MoonPhase.Phase](./apidocs/org/shredzone/commons/suncalc/MoonPhase.Phase.html) constants:

| Constant        | Description | Angle |
| --------------- | --- | ---:|
| `NEW_MOON`      | Moon is not illuminated (new moon) | 0° |
| `FIRST_QUARTER` | Half of the waxing moon is illuminated | 90° |
| `FULL_MOON`     | Moon is fully illuminated | 180° |
| `LAST_QUARTER`  | Half of the waning moon is illuminated | 270° |

Alternatively you can also pass any other angle (in degrees) to `phase()`.

Example:

```java
MoonPhase.compute().phase(MoonPhase.Phase.FULL_MOON);
```

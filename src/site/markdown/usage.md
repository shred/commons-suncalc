# Usage

`commons-suncalc` offers five astronomical calculations:

* [SunTimes](./apidocs/org/shredzone/commons/suncalc/SunTimes.html): Sunrise, sunset, noon and nadir times.
* [MoonTimes](./apidocs/org/shredzone/commons/suncalc/MoonTimes.html): Moonrise and moonset times.
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

### Special Parameters

Some classes offer further parameters. Please see the JavaDocs of their `Parameters` classes:

* [SunTimes](./apidocs/org/shredzone/commons/suncalc/SunTimes.Parameters.html)
* [MoonTimes](./apidocs/org/shredzone/commons/suncalc/MoonTimes.Parameters.html)

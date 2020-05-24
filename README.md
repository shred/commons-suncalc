# commons-suncalc ![build status](https://shredzone.org/badge/commons-suncalc.svg) ![maven central](https://shredzone.org/maven-central/org.shredzone.commons/commons-suncalc/badge.svg)

A Java library for calculation of sun and moon positions and phases.

## Features

* Lightweight, only requires Java 8 or higher, no other dependencies
* Android compatible, requires API level 26 (Android 8.0 "Oreo") or higher. For older Android versions, use [commons-suncalc v2](https://github.com/shred/commons-suncalc/tree/v2), which is similar to this version, but does not use the Java Date/Time API.
* Available at [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22commons-suncalc%22)
* Extensive unit tests

## Accuracy

Astronomical calculations are far more complex than throwing a few numbers into an obscure formula and then getting a fully accurate result. There is always a tradeoff between accuracy and computing time.

This library has its focus on getting _acceptable_ results at low cost, so it can also run on mobile devices, or devices with a low computing power. The results have an accuracy of about a minute, which should be good enough for common applications (like sunrise/sunset timers), but is probably not sufficient for astronomical purposes.

If you are looking for the highest possible accuracy, you are looking for a different library.

## Quick Start

This library consists of a few models, all of them are invoked the same way:

```java
ZonedDateTime dateTime = // date, time and timezone of calculation
double lat, lng = // geolocation
SunTimes times = SunTimes.compute()
        .on(dateTime)   // set a date
        .at(lat, lng)   // set a location
        .execute();     // get the results
System.out.println("Sunrise: " + times.getRise());
System.out.println("Sunset: " + times.getSet());
```

Read the [online documentation](https://shredzone.org/maven/commons-suncalc/) for examples and API details.

See the [migration guide](https://shredzone.org/maven/commons-suncalc/migration.html) for how to migrate from a previous version.

## References

This library bases on:

* "Astronomy on the Personal Computer", 4th edition, by Oliver Montenbruck and Thomas Pfleger
* "Astronomical Algorithms" by Jean Meeus

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-suncalc). Feel free to send pull requests.
* Found a bug? Please [file a bug report](https://github.com/shred/commons-suncalc/issues).

## License

_commons-suncalc_ is open source software. The source code is distributed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

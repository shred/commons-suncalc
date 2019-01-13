# commons-suncalc

A Java library for calculation of sun and moon positions and phases.

## Features

* Lightweight, only requires Java 1.7 or higher, no other dependencies
* <strike>Android compatible, requires API level 19 (KitKat) or higher</strike>  It should work for older android versions now! 
* <strike>Available at [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22commons-suncalc%22)</strike> my version is not on maven... 
* Extensive unit tests

## Accuracy

Astronomical calculations are far more complex than throwing a few numbers into an obscure formula and then getting a fully accurate result. There is always a tradeoff between accuracy and computing time.

This library has its focus on getting _acceptable_ results at low cost, so it can also run on mobile devices, or devices with a low computing power. The results have an accuracy of about a minute, which should be good enough for common applications (like sunrise/sunset timers), but is probably not sufficient for astronomical purposes.

If you are looking for the highest possible accuracy, you are looking for a different library.

## Quick Start

This library consists of a few models, all of them are invoked the same way:

```java
Date date = // requested date of calculation
double lat, lng = // geolocation
SunPosition position = SunPosition.compute()
        .on(date)       // set a date
        .at(lat, lng)   // set a location
        .execute();     // get the results
System.out.println("Elevation: " + position.getElevation() + "°");
System.out.println("Azimuth: " + position.getAzimuth() + "°");
```

See the [online documentation](https://shredzone.org/maven/commons-suncalc/) for API details.

See the [migration guide](https://shredzone.org/maven/commons-suncalc/migration.html) for how to migrate from a previous version.

## References

This library bases on:

* "Astronomy on the Personal Computer", 4th edition, by Oliver Montenbruck and Thomas Pfleger
* "Astronomical Algorithms" by Jean Meeus
* `MoonIllumination` is based on [suncalc](https://github.com/mourner/suncalc) by Vladimir Agafonkin

## License

_commons-suncalc_ is open source software. The source code is distributed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

# commons-suncalc ![build status](https://shredzone.org/badge/commons-suncalc.svg) ![maven central](https://maven-badges.herokuapp.com/maven-central/org.shredzone.commons/commons-suncalc/badge.svg)

A Java library for calculation of sun and moon positions and phases.

This library bases on the book "Astronomy on the Personal Computer" by Oliver Montenbruck and Thomas Pfleger.

_commons-suncalc_ version 2 is a complete rewrite. See the [migration guide](https://shredzone.org/maven/commons-suncalc/migration.html) for how to migrate from version 1.

## Features

* Lightweight, only requires Java 1.7 or higher
* Android compatible, requires API level 19 (KitKat) or higher
* Available at [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22commons-suncalc%22)

## BETA!

Version 2 has been almost rewritten from scratch, and uses a different set of formulas.

The results are unit-tested against independent results of public astronomical calculator web-sites. Still, please use this first release with care. There may still be errors in the calculations.

## Accuracy

Astronomical calculations are far more complex than throwing a few numbers into an obscure formula and then getting a fully accurate result. There is a tradeoff between accuracy and computing time.

This library has its focus on getting acceptable results at low cost, so it can also run on mobile devices, or devices with a low computing power. The results have an accuracy of about a minute, which should be good enough for common sunrise/sunset applications.

If you are looking for the highest possible accuracy, you are looking for a different library.

## Quick Start

This library consists of a few models, all of them are invoked the same way:

```java
Date date = // date of calculation
double lat, lng = // geolocation
SunPosition position = SunPosition.compute()
        .on(date)       // set a date
        .at(lat, lng)   // set a location
        .execute();     // get the results
System.out.println("Elevation: " + position.getElevation());
System.out.println("Azimuth: " + position.getAzimuth());
```

See the [online documentation](https://shredzone.org/maven/commons-suncalc/) for API.

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-suncalc). Feel free to send pull requests.
* Found a bug? [File a bug report](https://github.com/shred/commons-suncalc/issues).
* Are you getting different results from another library or web site? Please only report a bug if you can explain why you think the _commons-suncalc_ result is wrong.

## License

_commons-suncalc_ is open source software. The source code is distributed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

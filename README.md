# commons-suncalc ![build status](https://shredzone.org/badge/commons-suncalc.svg)

A Java library for calculation of sun and moon positions and phases.

It is a Java port of the great [SunCalc](https://github.com/mourner/suncalc) JavaScript library by [Vladimir Agafonkin](http://agafonkin.com/en/), with some modifications and Java-nizations to the API.

## Features

* Lightweight, only requires Java 1.7 or higher
* Android support
* Available at [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22commons-suncalc%22)

## Quick Start

This library consists of a few models, all of them are invoked the same way:

```java
Date date = // date of calculation
double lat, lng = // geolocation
SunPosition position = SunPosition.of(date, lat, lng);
System.out.println("Altitude: " + position.getAltitude());
System.out.println("Azimuth: " + position.getAzimuth());
```

See the [online documentation](https://shredzone.org/maven/commons-suncalc/) for API.

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-suncalc). Feel free to send pull requests.
* Found a bug? [File a bug report](https://github.com/shred/commons-suncalc/issues) if you think it is a bug in the Java port.
* For bugs in the calculation, and feature requests that are not related to the Java port, please [file a bug report at SunCalc](https://github.com/mourner/suncalc/issues).

## License

_commons-suncalc_ is open source software. The source code is distributed under the terms of [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

# GeoPackage Core Java

#### GeoPackage Core Libs ####

The [GeoPackage Libraries](http://ngageoint.github.io/GeoPackage/) were developed at the [National Geospatial-Intelligence Agency (NGA)](http://www.nga.mil/) in collaboration with [BIT Systems](http://www.bit-sys.com/). The government has "unlimited rights" and is releasing this software to increase the impact of government investments by providing developers with the opportunity to take things in new directions. The software use, modification, and distribution rights are stipulated within the [MIT license](http://choosealicense.com/licenses/mit/).

### Pull Requests ###
If you'd like to contribute to this project, please make a pull request. We'll review the pull request and discuss the changes. All pull request contributions to this project will be released under the MIT license.

Software source code previously released under an open source license and then modified by NGA staff is considered a "joint work" (see 17 USC § 101); it is partially copyrighted, partially public domain, and as a whole is protected by the copyrights of the non-government authors and must be released according to the terms of the original open source license.

### About ###

[GeoPackage Core](http://ngageoint.github.io/geopackage-core-java/) provides core functionality for [GeoPackage Library](http://ngageoint.github.io/GeoPackage/) implementations of the Open Geospatial Consortium [GeoPackage](http://www.geopackage.org/) [spec](http://www.geopackage.org/spec/).

It is the core library of the [GeoPackage Android](https://github.com/ngageoint/geopackage-android) SDK and [GeoPackage Java](https://github.com/ngageoint/geopackage-java) library, both which can be found under [OGC GeoPackage Implementations](http://www.geopackage.org/#implementations_nga) by the National Geospatial-Intelligence Agency.

### Usage ###

View the latest [Javadoc](http://ngageoint.github.io/geopackage-core-java/docs/api/)

#### GeoPackage Android ####

The [GeoPackage Android](https://github.com/ngageoint/geopackage-android) SDK is an Android GeoPackage implementation.

#### GeoPackage Android Map ####

The [GeoPackage Android Map](https://github.com/ngageoint/geopackage-android-map) SDK adds Android Map implementations to [GeoPackage Android](https://github.com/ngageoint/geopackage-android).

#### GeoPackage MapCache ####

The [GeoPackage MapCache](https://github.com/ngageoint/geopackage-mapcache-android) app provides an Android example of using the [GeoPackage Android Map](https://github.com/ngageoint/geopackage-android-map) SDK.

#### GeoPackage Java ####

The [GeoPackage Java](https://github.com/ngageoint/geopackage-java) library is a Java GeoPackage implementation.

### Installation ###

Pull from the [Maven Central Repository](http://search.maven.org/#artifactdetails|mil.nga.geopackage|geopackage-core|4.0.0|jar) (JAR, POM, Source, Javadoc)

    <dependency>
        <groupId>mil.nga.geopackage</groupId>
        <artifactId>geopackage-core</artifactId>
        <version>4.0.0</version>
    </dependency>

### Build ###

[![Build & Test](https://github.com/ngageoint/geopackage-core-java/workflows/Build%20&%20Test/badge.svg)](https://github.com/ngageoint/geopackage-core-java/actions?query=workflow%3A%22Build+%26+Test%22)

Build this repository using Eclipse and/or Maven:

    mvn clean install

### Remote Dependencies ###

* [Simple Features WKB](https://github.com/ngageoint/simple-features-wkb-java) (The MIT License (MIT)) - Simple Features Well Known Binary Lib
* [Simple Features WKT](https://github.com/ngageoint/simple-features-wkt-java) (The MIT License (MIT)) - Simple Features Well Known Text Lib
* [Simple Features Projection](https://github.com/ngageoint/simple-features-proj-java) (The MIT License (MIT)) - Simple Features Projection Lib
* [OGC API Features JSON](https://github.com/ngageoint/ogc-api-features-json-java) (The MIT License (MIT)) - OGC API Features JSON Lib
* [OrmLite](http://ormlite.com/) (Open Source License) - Object Relational Mapping (ORM) Library

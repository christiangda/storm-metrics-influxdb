# storm-metrics-influxdb 

[![Build Status](https://travis-ci.org/christiangda/storm-metrics-influxdb.svg?branch=master)](https://travis-ci.org/christiangda/)
[![codecov](https://codecov.io/gh/christiangda/storm-metrics-influxdb/branch/master/graph/badge.svg)](https://codecov.io/gh/christiangda/storm-metrics-influxdb)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.christiangda/storm-metrics-influxdb/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.christiangda/storm-metrics-influxdb)

#### Table of Contents
1. [Overview](#overview)
2. [Description - What it does and why it is useful](#description)
3. [Setup - The basics of getting started with it](#setup)
    * [Last Version and Repository](#version)
4. [Usage - Configuration options and additional functionality](#usage)
    * [Example](#example)
5. [Reference - An under-the-hood peek at what the module is doing and how](#reference)
5. [Limitations - OS compatibility, etc.](#limitations)
6. [Development - Guide for contributing to the module](#development)
7. [Authors - Who is contributing to do it](#authors)
8. [License](#license)

## Overview

This is an [Apache Storm](http://storm.apache.org/) [Metrics Consumer](http://storm.apache.org/releases/1.0.3/Metrics.html)
that forward its metrics to [InfluxDB](https://docs.influxdata.com/influxdb/v1.2/) to be used in real-time.  Then you can use
[Grafana](http://grafana.org/) to make a [Dashboard](http://grafana.org/features/) with your metrics.

## Description

[Apache Storm](http://storm.apache.org/) has a powerful metrics engine as you can see in [Apache Storm Metrics](http://storm.apache.org/releases/1.0.3/Metrics.html),
and we can implement our Own [Metrics Consumer](http://storm.apache.org/releases/1.0.3/Metrics.html).
This is what exactly it does, it implements a Metrics Consumer that send all of Metrics collected
by Apache Storm to [InfluxDB](https://docs.influxdata.com/influxdb/v1.2/).

## Setup

### Version

The actual version of artifacts are:

**Releases artifacts**

Server: [Maven release repository](https://oss.sonatype.org/content/groups/staging/com/github/christiangda/storm-metrics-influxdb)

```xml
<dependency>
    <groupId>com.github.christiangda</groupId>
    <artifactId>storm-metrics-influxdb</artifactId>
    <version>See release version in the link upper</version>
</dependency>
```

**SNAPSHOT artifacts**

Server: [Maven snaphot repository](https://oss.sonatype.org/content/groups/public/com/github/christiangda/storm-metrics-influxdb)

```xml
<dependency>
  <groupId>com.github.christiangda</groupId>
    <artifactId>storm-metrics-influxdb</artifactId>
    <version>See release version in the link upper</version>
</dependency>
```

## Usage

**Applying Metrics Collector to a specific topology**

```
...
    topologyConf.registerMetricsConsumer(com.github.christiangda.storm.metrics.InfluxDBMetricsConsumer.class, 1);
    topologyConf.put("metrics.reporter.name", "InfluxDBMetricsConsumer");
    topologyConf.put("metrics.influxdb.url", "<http://YOUR_INFLUXDB_HOSTNAME:PORT>");
    topologyConf.put("metrics.influxdb.username", "<YOUR_INFLUXDB_USERNAME>");
    topologyConf.put("metrics.influxdb.password", "<YOUR_INFLUXDB_PASSWORD>");
    topologyConf.put("metrics.influxdb.database", "<YOUR_INFLUXDB_DATABASE>");
    topologyConf.put("metrics.influxdb.measurement.prefix", "<YOUR_INFLUXDB_MEASUREMENT_PREFIX>");
    topologyConf.put("metrics.influxdb.enable.gzip", <true or false>);
...
```

**Applying Metrics Collector to all Apache Storm Cluster**

```yaml
topology.metrics.consumer.register:
  - class: "com.github.christiangda.storm.metrics.InfluxDBMetricsConsumer"
    parallelism.hint: 1
    argument:
      metrics.reporter.name: "com.github.christiangda.storm.metrics.InfluxDBMetricsConsumer"
      metrics.influxdb.url: "http://YOUR_INFLUXDB_HOSTNAME:PORT"
      metrics.influxdb.username: "YOUR_INFLUXDB_USERNAME"
      metrics.influxdb.password: "YOUR_INFLUXDB_PASSWORD"
      metrics.influxdb.database: "YOUR_INFLUXDB_DATABASE"
      metrics.influxdb.measurement.prefix: "YOUR_INFLUXDB_MEASUREMENT_PREFIX"
      metrics.influxdb.enable.gzip: true
```

### Example

I've created a Maven Apache Storm project to show you an example, if you want to view it
go to [test-storm-metrics-influxdb](https://github.com/christiangda/test-storm-metrics-influxdb)

## Reference

* [Apache Storm](http://storm.apache.org/)
* [Apache Storm Metrics Consumer](http://storm.apache.org/releases/1.0.3/Metrics.html)
* [Apache Storm Configuration](http://storm.apache.org/releases/1.0.3/Configuration.html)
* [InfluxDB](https://docs.influxdata.com/influxdb/v1.2/)
* [InfluxDB Java API](https://github.com/influxdata/influxdb-java)
* [Grafana](http://grafana.org/)

## Limitations

* [Apache Storm](http://storm.apache.org/) >= 1.0.0
* [InfluxDB](https://docs.influxdata.com/influxdb/v1.2/) >= 1.0.0
* [Java](https://www.java.com/es/download/help/index_installing.xml?j=7) >= 7

## Development

* [Fork it](https://github.com/christiangda/storm-metrics-influxdb#fork-destination-box) / [Clone it](https://github.com/christiangda/storm-metrics-influxdb.git) (`git clone https://github.com/christiangda/storm-metrics-influxdb.git; cd storm-metrics-influxdb`)
* Create your feature branch (`git checkout -b my-new-feature`)
* Install [Maven](https://maven.apache.org/install.html)
* Make your changes / improvements / fixes / etc, and of course **your Unit Test** for new code
* Run the tests (`mvn clean compile test package`)
* Commit your changes (`git add . && git commit -m 'Added some feature'`)
* Push to the branch (`git push -u origin my-new-feature`)
* [Create new Pull Request](https://github.com/christiangda/storm-metrics-influxdb/pull/new/master)

**Of course, bug reports and suggestions for improvements are always welcome.**

You can also support my work on powerdns via

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://paypal.me/christiangda)

This project use [Maven Release Plugin](http://maven.apache.org/maven-release/maven-release-plugin/) to do easy the process
of deploy to [Github](https://github.com/christiangda/storm-metrics-influxdb) repository and [Maven Repository](https://oss.sonatype.org/content/groups/staging)


## Authors

* [Christian González](https://github.com/christiangda) 
* [Nikolay Melnikov](https://github.com/melnikovkolya)

## License

This module is released under the Apache License Version 2.0:

* [http://www.apache.org/licenses/LICENSE-2.0.html](http://www.apache.org/licenses/LICENSE-2.0.html)

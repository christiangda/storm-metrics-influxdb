# storm-metrics-influxdb

#### Table of Contents
1. [Overview](#overview)
2. [Description - What it does and why it is useful](#description)
3. [Setup - The basics of getting started with it](#setup)
    * [Last Version and Repository](#version)
    * [Using Maven](#using-Maven)
    * [Using SBT](#using-sbt)
    * [Using Gradle](#using-gradle)
4. [Usage - Configuration options and additional functionality](#usage)
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

Server: [Maven release repository](https://mvnrepository.com/artifact/com.github.christiangda/storm-metrics-influxdb)

```xml
<dependency>
    <groupId>com.github.christiangda.storm</groupId>
    <artifactId>storm-metrics-influxdb</artifactId>
    <version>See release version in the link upper</version>
</dependency>
```

**SNAPSHOT artifacts**

Server: [Maven snaphot repository]()

```xml
<dependency>
  <groupId>com.github.christiangda.storm</groupId>
    <artifactId>storm-metrics-influxdb</artifactId>
    <version>See release version in the link upper (remember)</version>
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

This project use [Maven Release Plugin](http://maven.apache.org/maven-release/maven-release-plugin/) to do easy the process
of deploy to [Github](https://github.com/christiangda/storm-metrics-influxdb) repository and [Maven Repository]()

### Create a SNAPSHOT

```
mvn clean compile test
# git add .  #
# git commit -m "comment about my last change" # this will trigering the github webhook and jenkins build

mvn deploy
```

### Create a Release

```
mvn clean test
# git add .
# git commit -m "comment about my last change"  # this will trigering the github webhook and jenkins build

mvn release:prepare
mvn release:perform
```

## Authors

* [Christian González](https://github.com/christiangda) `<christian at gmail dot com>`

## License

This module is released under the GNU General Public License Version 3:

* [http://www.gnu.org/licenses/gpl-3.0-standalone.html](http://www.gnu.org/licenses/gpl-3.0-standalone.html)

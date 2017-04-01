# How to configure SBT Project

 In your project `build.sbt` file you need to add the following
 
```scala
resolvers ++= Seq( "nexus.tuguu.com-thirdparty" at "https://nexus.tguhost.com/content/repositories/thirdparty",
                   "nexus.tuguu.com-thirdparty-snapshots" at "https://nexus.tguhost.com/content/repositories/thirdparty-snapshots",
                   "nexus.tuguu.com-releases" at "https://nexus.tguhost.com/content/repositories/releases",
                   "nexus.tuguu.com-staging" at "https://nexus.tguhost.com/content/repositories/staging",
                   "nexus.tuguu.com-snapshots" at "https://nexus.tguhost.com/content/repositories/snapshots"
)
```

and you have configured your nexus server account correctly in `~/.sbt/0.13/sonatype.sbt` file (it depend of sbt version),  it need to looks like

```scala
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "nexus.tuguu.com-thirdparty",
                           "your username for nexus server",
                           "your password for nexus server");
                           
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "nexus.tuguu.com-thirdparty-snapshots",
                           "your username for nexus server",
                           "your password for nexus server");
                           
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "nexus.tuguu.com-releases",
                           "your username for nexus server",
                           "your password for nexus server");
                           
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "nexus.tuguu.com-staging",
                           "your username for nexus server",
                           "your password for nexus server");
                           
credentials += Credentials("Sonatype Nexus Repository Manager",
                           "nexus.tuguu.com-snapshots",
                           "your username for nexus server",
                           "your password for nexus server");                                                                                                            
```

After that, you can add the dependency using the following

**Releases artifacts**

```scala
libraryDependencies ++= Seq("com.txrlabs.storm" % "storm-metrics-influxdb" % "see artifact.version")
```

**SNAPSHOT artifacts**

```scala
libraryDependencies ++= Seq("com.txrlabs.storm" % "storm-metrics-influxdb" % "see artifact.version-SNAPSHOT" changing())
```

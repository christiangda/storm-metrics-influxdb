# How to configure Gradle Project

In your project `build.gradle` file you need to add the following
 
```gradle
repositories {
    maven { url 'https://nexus.tguhost.com/content/repositories/thirdparty'
            credentials {
                username "$mavenUser"
                password "$mavenPassword"
            }
    }
    maven { url 'https://nexus.tguhost.com/content/repositories/thirdparty-snapshots' 
            credentials {
                username "$mavenUser"
                password "$mavenPassword"
            }
    }
    maven { url 'https://nexus.tguhost.com/content/repositories/releases' 
            credentials {
                username "$mavenUser"
                password "$mavenPassword"
            }
    }
    maven { url 'https://nexus.tguhost.com/content/repositories/staging' 
            credentials {
                username "$mavenUser"
                password "$mavenPassword"
            }
    }
    maven { url 'https://nexus.tguhost.com/content/repositories/snapshots'
            credentials {
                username "$mavenUser"
                password "$mavenPassword"
            }
    }
    mavenCentral()
}
```

and you have configured your nexus server account correctly in `~/.gradle/gradle.properties` file,  it need to looks like

```ini
mavenUser=your username for nexus server
mavenPassword=your password for nexus server                                                                        
```

After that, you can add the dependency using the following

**Releases artifacts**

```sbt
dependencies{
    compile('com.txrlabs.storm:storm-metrics-influxdb:<see artifact.version>')
}
```

**SNAPSHOT artifacts**

```sbt
dependencies{
    compile('com.txrlabs.storm:storm-metrics-influxdb:<see artifact.version-SNAPSHOT>') { changing = true }
}
```

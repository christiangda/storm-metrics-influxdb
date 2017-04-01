# How to configure Maven Project

 In your project `pom.xml` file you need to add the following
 
```xml
<repositories>
    <repository>
        <id>nexus.tuguu.com-thirdparty</id>
        <url>https://nexus.tguhost.com/content/repositories/thirdparty</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <releases>
            <enabled>true</enabled>
        </releases>
    </repository>
    <repository>
        <id>nexus.tuguu.com-thirdparty-snapshots</id>
        <url>https://nexus.tguhost.com/content/repositories/thirdparty-snapshots</url>
        <layout>default</layout>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
    <repository>
        <id>nexus.tuguu.com-releases</id>
        <url>https://nexus.tguhost.com/content/repositories/releases</url>
        <layout>default</layout>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
    <repository>
        <id>nexus.tuguu.com-staging</id>
        <url>https://nexus.tguhost.com/content/repositories/staging</url>
        <layout>default</layout>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
    <repository>
        <id>nexus.tuguu.com-snapshots</id>
        <url>https://nexus.tguhost.com/content/repositories/snapshots</url>
        <layout>default</layout>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>warn</checksumPolicy>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>    
</repositories>
```

and you have configured your nexus server account correctly in `~/.m2/settings.xml` file,  it need to looks like

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
  https://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <!-- https://nexus.tguhost.com -->
    <server>
      <id>nexus.tuguu.com-thirdparty</id>
      <username>your username for nexus server</username>
      <password>your password for nexus server</password>
    </server>
    <server>
      <id>nexus.tuguu.com-thirdparty-snapshots</id>
      <username>your username for nexus server</username>
      <password>your password for nexus server</password>
    </server>
    <server>
      <id>nexus.tuguu.com-releases</id>
      <username>your username for nexus server</username>
      <password>your password for nexus server</password>
    </server>
    <server>
      <id>nexus.tuguu.com-staging</id>
      <username>your username for nexus server</username>
      <password>your password for nexus server</password>
    </server>
    <server>
      <id>nexus.tuguu.com-snapshots</id>
      <username>your username for nexus server</username>
      <password>your password for nexus server</password>
    </server>
  </servers>
</settings>
```

After that, you can add the dependency using the following

**Releases artifacts**

```xml
<dependency>
    <groupId>com.txrlabs.storm</groupId>
    <artifactId>storm-metrics-influxdb</artifactId>
    <version>see artifact.version</version>
</dependency>
```

**SNAPSHOT artifacts**

```xml
<dependency>
    <groupId>com.txrlabs.storm</groupId>
    <artifactId>storm-metrics-influxdb</artifactId>
    <version>see artifact.version-SNAPSHOT</version>
</dependency>

```
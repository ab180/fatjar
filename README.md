# FatJar

[![Download](https://api.bintray.com/packages/ab180/gradle-plugin/fatjar/images/download.svg)](https://bintray.com/ab180/gradle-plugin/fatjar/_latestVersion)

## Getting started

Add snippet below to your root build script file:

```gradle
buildscript {
    repositories {
        maven {
            url uri("/Users/wontak/Desktop/plugins")
        }
    }
    dependencies {
        classpath "co.ab180:fatjar:{$latest_version}"
    }
}
```

Add snippet below to the `build.gradle` of your android library:

```gradle
apply plugin: "co.ab180.fatjar"
```

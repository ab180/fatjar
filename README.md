# FatJar

[![Download](https://api.bintray.com/packages/ab180/gradle-plugin/fatjar/images/download.svg)](https://bintray.com/ab180/gradle-plugin/fatjar/_latestVersion)

## Getting started

### Step 1: Apply plugin

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

### Step 2: Internalize dependencies

Change `implementation` or `api` to `internalize` while you want to embed the dependency in the library. Like this:

```gradle
dependencies {
    // Local dependency
    internalize project(":library")
    internalize(name:"fantastic_library", ext:"jar")
    
    // Remote dependency
    internalize "com.google.code.gson:gson:1.+"
}
```

> FatJar currently only support `.jar` extension under the `com.android.library` plugin

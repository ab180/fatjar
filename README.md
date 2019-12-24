# FatJar

[![Download](https://api.bintray.com/packages/ab180/gradle-plugin/fatjar/images/download.svg)](https://bintray.com/ab180/gradle-plugin/fatjar/_latestVersion)

## Getting started

### Step 1 : Apply plugin

Add snippet below to your root build script file.

```gradle
buildscript {
    repositories {
        maven {
            url "https://dl.bintray.com/ab180/gradle-plugin"
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

### Step 2 : Internalize dependencies

Change `implementation` or `api` to `internalize` while you want to embed the dependency in the library.

```gradle
dependencies {
    // Local dependency
    internalize files("fantastic_library.jar")
    
    // Remote dependency
    internalize "com.google.code.gson:gson:1.+"
}
```

> FatJar currently support `.jar` extension under the `com.android.library` plugin only

### Optional : Change package name

Add repackage scope into `build.gradle` file.

```gradle
repackage {
    def prefix = "com.mycompany"
    relocate "com.google.gson.**", "$prefix.com.google.gson.@1"
    relocate "kotlin.**", "$prefix.kotlin.@1"
}
```

## TODO

- Mixing [JarJar][1] library inside like [ShadowJar][2]

## Thanks

- [android-fat-aar][3]
- [fat-aar-plugin][4]
- [fat-aar-android][5]

[1]: https://code.google.com/archive/p/jarjar
[2]: https://github.com/johnrengelman/shadow
[3]: https://github.com/adwiv/android-fat-aar
[4]: https://github.com/Vigi0303/fat-aar-plugin
[5]: https://github.com/kezong/fat-aar-android

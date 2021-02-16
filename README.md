# FatJar

[![Download](https://img.shields.io/maven-metadata/v?metadataUrl=https://sdk-download.airbridge.io/maven/co/ab180/fatjar/maven-metadata.xml)](https://sdk-download.airbridge.io/maven/co/ab180/fatjar/maven-metadata.xml)

## Getting started

### Step 1 : Apply plugin

Add snippet below to your root build script file.

```gradle
buildscript {
    repositories {
        maven {
            url "https://sdk-download.airbridge.io/maven"
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
    
    // When 'transitive=true' is needed
    internalizeAll org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.+"
}
```

> FatJar currently support `.jar` extension under the `com.android.library` plugin only

### Optional : Change package name (Shadow)

Add repackage scope into `build.gradle` file.

```gradle
repackage {
    def prefix = "com.mycompany"
    relocate "com.google.gson.**", "${prefix}.com.google.gson.@1"
    relocate "kotlin.**", "${prefix}.kotlin.@1"
}
```

## Thanks

- [JarJar][1]
- [android-fat-aar][3]
- [fat-aar-plugin][4]
- [fat-aar-android][5]

[1]: https://code.google.com/archive/p/jarjar
[2]: https://github.com/johnrengelman/shadow
[3]: https://github.com/adwiv/android-fat-aar
[4]: https://github.com/Vigi0303/fat-aar-plugin
[5]: https://github.com/kezong/fat-aar-android

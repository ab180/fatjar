plugins {
    id("groovy")
    id("maven-publish")
}

repositories {
    google()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "com.github.ab180"
            artifactId = "fatjar"
            version = "2.0.0"
        }
    }
    repositories {
        maven {
            name = "local"
            url = uri("$buildDir/maven")
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())

    implementation("com.android.tools.build:gradle:4.2.2")
    implementation("org.zeroturnaround:zt-zip:1.13")
    implementation("io.github.g00fy2:versioncompare:1.4.1")
}
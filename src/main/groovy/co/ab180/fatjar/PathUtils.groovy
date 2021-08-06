package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import io.github.g00fy2.versioncompare.Version

class PathUtils {

    static String buildPreBuildTaskPath(LibraryVariant variant) {
        return "pre${variant.name.capitalize()}Build"
    }

    static String buildBundleTaskPath(LibraryVariant variant) {
        return "bundle${variant.name.capitalize()}"
    }

    static String buildBundleAarTaskPath(LibraryVariant variant) {
        return "bundle${variant.name.capitalize()}Aar"
    }

    static String buildBundleLibRuntimeTaskPath(LibraryVariant variant, Version gradleToolVersion) {
        if (gradleToolVersion.isAtLeast("4.2.0")) {
            return "bundleLibRuntimeToDir${variant.name.capitalize()}"
        } else if (gradleToolVersion.isAtLeast("4.0.0")) {
            return "bundleLibRuntimeToJar${variant.name.capitalize()}"
        } else {
            return "bundleLibRuntime${variant.name.capitalize()}"
        }
    }

    static String transformClassesAndResourcesWithSyncLibJarsTask(LibraryVariant variant, Version gradleToolVersion) {
        if (gradleToolVersion.isAtLeast("3.6.0")) {
            return "sync${variant.name.capitalize()}LibJars"
        } else {
            return "transformClassesAndResourcesWithSyncLibJarsFor${variant.name.capitalize()}"
        }
    }
}

package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant

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

    static String buildBundleLibRuntimeTaskPath(LibraryVariant variant) {
        return "bundleLibRuntime${variant.name.capitalize()}"
    }

    static String transformClassesAndResourcesWithSyncLibJarsTask(LibraryVariant variant) {
        return "transformClassesAndResourcesWithSyncLibJarsFor${variant.name.capitalize()}"
    }
}

package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import io.github.g00fy2.versioncompare.Version
import org.gradle.api.Project

class FileUtils {

    private static final AAR_LIBS_DIRECTORY = "aar_libs_directory"
    private static final AAR_MAIN_JAR = "aar_main_jar"
    private static final INTERMEDIATES = "intermediates"
    private static final JAVAC = "javac"
    private static final PACKAGED_CLASSES = "packaged-classes"

    static File createLibsDirFile(Project project, LibraryVariant variant, Version gradleToolVersion) {
        if (gradleToolVersion.isAtLeast("3.6.0")) {
            String path = "${project.buildDir.path}/$INTERMEDIATES/$AAR_LIBS_DIRECTORY/${variant.dirName}/libs"
            return project.file(path)
        } else {
            String path = "${project.buildDir.path}/$INTERMEDIATES/$PACKAGED_CLASSES/${variant.dirName}/libs"
            return project.file(path)
        }
    }

    static File createClassesDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$JAVAC/${variant.dirName}/classes"
        return project.file(path)
    }

    static File createJarsDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$PACKAGED_CLASSES/${variant.dirName}/libs"
        return project.file(path)
    }

    static File createPackagedClassesJarFile(Project project, LibraryVariant variant, Version gradleToolVersion) {
        if (gradleToolVersion.isAtLeast("3.6.0")) {
            String path = "${project.buildDir.path}/$INTERMEDIATES/$AAR_MAIN_JAR/${variant.dirName}/classes.jar"
            return project.file(path)
        } else {
            String path = "${project.buildDir.path}/$INTERMEDIATES/$PACKAGED_CLASSES/${variant.dirName}/classes.jar"
            return project.file(path)
        }
    }

    static String getFileExtension(File file) {
        String name = file.getName()
        int lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1) {
            return "" // empty extension
        }
        return name.substring(lastIndexOf + 1)
    }
}

package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project

class FileUtils {

    private static final INTERMEDIATES = "intermediates"
    private static final JAVAC = "javac"
    private static final PACKAGED_CLASSES = "packaged-classes"

    static File createLibsDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$PACKAGED_CLASSES/${variant.dirName}/libs"
        return project.file(path)
    }

    static File createClassesDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$JAVAC/${variant.dirName}/classes"
        return project.file(path)
    }

    static File createJarsDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$PACKAGED_CLASSES/${variant.dirName}/libs"
        return project.file(path)
    }

    static File createPackagedClassesJarFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$PACKAGED_CLASSES/${variant.dirName}/classes.jar"
        return project.file(path)
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

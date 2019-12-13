package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection

class FileUtils {

    private static final INTERMEDIATES = "intermediates"
    private static final BUNDLES = "bundles"
    private static final JAVAC = "javac"

    static File createLibsDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$JAVAC/${variant.dirName}/classes"
        return project.file(path)
    }

    static File createClassesDirFile(Project project, LibraryVariant variant) {
        String path = "${project.buildDir.path}/$INTERMEDIATES/$BUNDLES/${variant.dirName}/libs"
        return project.file(path)
    }
}

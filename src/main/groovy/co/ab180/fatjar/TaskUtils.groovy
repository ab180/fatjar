package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Project
import org.gradle.api.Task

import java.lang.ref.WeakReference

class TaskUtils {

    private static WeakReference<Project> projectRef

    static void init(Project project) {
        projectRef = new WeakReference<>(project)
    }

    static Task findPreBuildTask(LibraryVariant variant) {
        Project project = projectRef.get()
        if (project == null) {
            return null
        }

        String taskPath = PathUtils.buildPreBuildTaskPath(variant)
        return project.tasks.findByPath(taskPath)
    }

    static Task findBundleTask(LibraryVariant variant) {
        Project project = projectRef.get()
        if (project == null) {
            return null
        }

        String taskPath = PathUtils.buildBundleTaskPath(variant)
        Task bundleTask = project.tasks.findByPath(taskPath)
        if (bundleTask == null) {
            taskPath = PathUtils.buildBundleAarTaskPath(variant)
            bundleTask = project.tasks.findByPath(taskPath)
        }
        return bundleTask
    }

    static Task findTransformClassesAndResourcesWithSyncLibJars(LibraryVariant variant) {
        Project project = projectRef.get()
        if (project == null) {
            return null
        }

        String taskPath = PathUtils.transformClassesAndResourcesWithSyncLibJarsTask(variant)
        return project.tasks.findByPath(taskPath)
    }

    static Task findJavaCompileTask(LibraryVariant variant) {
        return variant.getJavaCompileProvider().get()
    }

    static Task createClassesMergeTask(Project project, LibraryVariant variant, List<File> jarFiles) {
        Task task = project.tasks.create("mergeClasses${variant.name.capitalize()}")
        task.doFirst {
            File classesDir = FileUtils.createClassesDirFile(project, variant)
            for (jarFile in jarFiles) {
                LoggingUtils.info("%s copy to %s", jarFile.path, classesDir.path)
                project.copy {
                    from project.zipTree(jarFile)
                    into classesDir
                    exclude "META-INF/"
                }
            }
        }
        return task
    }
}

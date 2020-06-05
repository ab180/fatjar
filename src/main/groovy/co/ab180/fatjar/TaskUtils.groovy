package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import com.g00fy2.versioncompare.Version
import com.tonicsystems.jarjar.MainProcessor
import com.tonicsystems.jarjar.Rule
import com.tonicsystems.jarjar.util.StandaloneJarProcessor
import groovy.io.FileType
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.bundling.Zip

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

    static Task findBundleLibRuntimeTask(LibraryVariant variant, Version gradleToolVersion) {
        Project project = projectRef.get()
        if (project == null) {
            return null
        }

        String taskPath = PathUtils.buildBundleLibRuntimeTaskPath(variant, gradleToolVersion)
        return project.tasks.findByPath(taskPath)
    }

    static Task findTransformClassesAndResourcesWithSyncLibJars(LibraryVariant variant, Version gradleToolVersion) {
        Project project = projectRef.get()
        if (project == null) {
            return null
        }

        String taskPath = PathUtils.transformClassesAndResourcesWithSyncLibJarsTask(variant, gradleToolVersion)
        return project.tasks.findByPath(taskPath)
    }

    static Task findJavaCompileTask(LibraryVariant variant) {
        return variant.getJavaCompileProvider().get()
    }

    static Task createMergeClassesTask(Project project, LibraryVariant variant, List<File> jarFiles) {
        Task task = project.tasks.create("mergeClasses${variant.name.capitalize()}")
        applyMergeClassesTask(project, variant, task, jarFiles)
        return task
    }

    static Task createMergeClassesRuntimeTask(Project project, LibraryVariant variant, List<File> jarFiles) {
        Task task = project.tasks.create("mergeClassesRuntime${variant.name.capitalize()}")
        applyMergeClassesTask(project, variant, task, jarFiles)
        return task
    }

    private static void applyMergeClassesTask(Project project, LibraryVariant variant, Task task, List<File> jarFiles) {
        task.doFirst {
            File classesDir = FileUtils.createClassesDirFile(project, variant)
            for (jarFile in jarFiles) {
                project.copy {
                    LoggingUtils.println("Copy '${jarFile.path}' to '${classesDir.path}'")
                    from project.zipTree(jarFile)
                    into classesDir
                    exclude "META-INF/"
                }
            }
        }
    }

    static Task createMergeJarsTask(Project project, LibraryVariant variant, Version gradleToolVersion, List<File> jarFiles) {
        Task task = project.tasks.create("mergeJars${variant.name.capitalize()}")
        task.doFirst {
            File libsDir = FileUtils.createLibsDirFile(project, variant, gradleToolVersion)
            for (jarFile in jarFiles) {
                project.copy {
                    LoggingUtils.println("Copy '${jarFile.path}' to '${libsDir.path}'")
                    from jarFile
                    into libsDir
                }
            }
        }
        return task
    }

    static Task createExcludeAllMetaInfoInMergedJarsTask(Project project, LibraryVariant variant, Version gradleToolVersion) {
        Task task = project.tasks.create("excludeAllMetaInfoInMergedJars${variant.name.capitalize()}")
        task.doFirst {
            File libsDir = FileUtils.createLibsDirFile(project, variant, gradleToolVersion)
            libsDir.eachFileMatch(FileType.FILES, ~/^.*-.*?.jar$/) { jarFile ->
                LoggingUtils.println("Unzipping '${jarFile.path}'")
                JarUtils.excludeMetaInfo(project, jarFile)
            }
        }
        return task
    }

    static Task createRepackageJarTask(Project project, LibraryVariant variant, Version gradleToolVersion, List<Rule> rules) {
        Task task = project.tasks.create("repackageJar${variant.name.capitalize()}")
        task.doFirst {
            boolean verbose = Boolean.getBoolean("verbose")
            boolean skipManifest = Boolean.getBoolean("skipManifest")
            MainProcessor proc = new MainProcessor(rules, verbose, skipManifest)

            // Repackage '/libs' jar files
            File libsDir = FileUtils.createLibsDirFile(project, variant, gradleToolVersion)
            libsDir.eachFileMatch(FileType.FILES, ~/^.*-.*?.jar$/) { jarFile ->
                LoggingUtils.println("Repackage to '${jarFile.path}'")
                StandaloneJarProcessor.run(jarFile, jarFile, proc)
            }

            // Repackage 'classes' jar file
            File packagedClassesJarFile = FileUtils.createPackagedClassesJarFile(project, variant, gradleToolVersion)
            LoggingUtils.println("Repackage to '${packagedClassesJarFile.path}'")
            StandaloneJarProcessor.run(packagedClassesJarFile, packagedClassesJarFile, proc)
        }
        return task
    }
}

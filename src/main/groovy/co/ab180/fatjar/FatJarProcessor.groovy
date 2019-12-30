package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import com.tonicsystems.jarjar.Rule
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ResolvedArtifact

class FatJarProcessor {

    private Project project
    private LibraryVariant variant
    private List<File> jarFiles
    private List<Rule> rules

    FatJarProcessor(Project project, LibraryVariant variant, List<ResolvedArtifact> resolvedArtifacts, List<Rule> rules) {
        this.project = project
        this.variant = variant
        this.jarFiles = new ArrayList<>()
        this.rules = new ArrayList<>(rules)
        for (artifact in resolvedArtifacts) {
            File file = artifact.file
            if (file.exists()) {
                this.jarFiles.add(file)
            }
        }

        project.rootProject.buildscript.getConfigurations().getByName("classpath").getDependencies().each { dependency ->
            if (dependency.group == "com.android.tools.build" && dependency.name == "gradle") {
                validateAndroidGradleToolVersion(dependency.version)
            }
        }
    }

    void process() {
        Task preBuildTask = TaskUtils.findPreBuildTask(variant)
        if (preBuildTask == null) {
            throw new RuntimeException("Task not found :: preBuildTask")
        }

        clearCache()
        buildClassesWithJars()
    }

    private void clearCache() {
        FileUtils.createLibsDirFile(project, variant).deleteDir()
        FileUtils.createClassesDirFile(project, variant).deleteDir()
    }

    private void buildClassesWithJars() {
        Task syncLibJarsTask = TaskUtils.findTransformClassesAndResourcesWithSyncLibJars(variant)
        if (syncLibJarsTask == null) {
            throw new RuntimeException("Task not found :: transformClassesAndResourcesWithSyncLibJarsTask")
        }

        Task javaCompileTask = TaskUtils.findJavaCompileTask(variant)
        Task bundleTask = TaskUtils.findBundleTask(variant)
        Task mergeClassesTask = TaskUtils.createMergeClassesTask(project, variant, jarFiles)
        Task repackageJarTask = TaskUtils.createRepackageJarTask(project, variant, rules)

        // Create task dependency
        mergeClassesTask.dependsOn(javaCompileTask)
        syncLibJarsTask.dependsOn(mergeClassesTask)
        repackageJarTask.dependsOn(syncLibJarsTask)
        bundleTask.dependsOn(repackageJarTask)

        // Runtime
        Task mergeClassesRuntimeTask = TaskUtils.createMergeClassesRuntimeTask(project, variant, jarFiles)
        Task bundleLibRuntimeTask = TaskUtils.findBundleLibRuntimeTask(variant)
        bundleLibRuntimeTask.dependsOn(mergeClassesRuntimeTask)
    }

    private static void validateAndroidGradleToolVersion(String version) {
        String[] versionSplits = version.split("[._]")
        Integer major = versionSplits[0].toInteger()
        Integer minor = versionSplits[1].toInteger()

        if (major < 3) {
            throw new RuntimeException("FatJar only support 'com.android.tools.build' version above 3.5.0")
        } else {
            if (minor < 5) {
                throw new RuntimeException("FatJar only support 'com.android.tools.build' version above 3.5.0")
            }
        }
    }
}

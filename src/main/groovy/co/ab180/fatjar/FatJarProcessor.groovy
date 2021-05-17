package co.ab180.fatjar

import com.android.build.gradle.api.LibraryVariant
import io.github.g00fy2.versioncompare.Version
import com.tonicsystems.jarjar.Rule
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ResolvedArtifact

class FatJarProcessor {

    private Project project
    private LibraryVariant variant
    private List<File> jarFiles
    private List<Rule> rules
    private Version gradleToolVersion

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
                gradleToolVersion = new Version(dependency.version)
                if (gradleToolVersion.isLowerThan("3.5.0")) {
                    throw new RuntimeException("FatJar only support 'com.android.tools.build' version above 3.5.0")
                }
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
        FileUtils.createLibsDirFile(project, variant, gradleToolVersion).deleteDir()
        FileUtils.createClassesDirFile(project, variant).deleteDir()
    }

    private void buildClassesWithJars() {
        Task syncLibJarsTask = TaskUtils.findTransformClassesAndResourcesWithSyncLibJars(variant, gradleToolVersion)
        if (syncLibJarsTask == null) {
            throw new RuntimeException("Task not found :: transformClassesAndResourcesWithSyncLibJarsTask")
        }

        Task bundleTask = TaskUtils.findBundleTask(variant)
        Task mergeJarsTask = TaskUtils.createMergeJarsTask(project, variant, gradleToolVersion, jarFiles)
        Task excludeMetaInfoTask = TaskUtils.createExcludeAllMetaInfoInMergedJarsTask(project, variant, gradleToolVersion)
        Task repackageJarTask = TaskUtils.createRepackageJarTask(project, variant, gradleToolVersion, rules)

        // Create task dependency
        mergeJarsTask.mustRunAfter(syncLibJarsTask)
        excludeMetaInfoTask.dependsOn(mergeJarsTask)
        repackageJarTask.dependsOn(excludeMetaInfoTask)
        bundleTask.dependsOn(repackageJarTask)

        // Runtime
        Task mergeClassesRuntimeTask = TaskUtils.createMergeClassesRuntimeTask(project, variant, jarFiles)
        Task bundleLibRuntimeTask = TaskUtils.findBundleLibRuntimeTask(variant, gradleToolVersion)
        bundleLibRuntimeTask.dependsOn(mergeClassesRuntimeTask)
    }
}

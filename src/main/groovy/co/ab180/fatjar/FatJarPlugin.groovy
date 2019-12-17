package co.ab180.fatjar

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.artifacts.*

class FatJarPlugin implements Plugin<Project> {

    private static final String ANDROID_LIBRARY_PLUGIN_NAME = "com.android.library"
    private static final String CONFIGURATION_NAME = "internalize"
    private static final String JAR = "jar"
    private static final String COMPILE_ONLY = "compileOnly"

    private Configuration configuration
    private List<ResolvedArtifact> resolvedArtifacts
    private List<File> resolvedFiles

    @Override
    void apply(Project project) {
        LoggingUtils.init(project.logger)
        TaskUtils.init(project)
        validateAndroidPluginIncluded(project)
        configuration = createConfiguration(project)
        buildCompileOnlyDependencies(project, configuration)
        project.afterEvaluate {
            resolvedArtifacts = findAllResolvedArtifacts(configuration)
            resolvedFiles = findAllUnResolvedFiles(configuration)
            project.android.libraryVariants.all { variant ->
                FatJarProcessor processor = new FatJarProcessor(project, variant, resolvedArtifacts, resolvedFiles)
                processor.process()
            }
        }
    }

    private static void validateAndroidPluginIncluded(Project project) {
        if (!project.plugins.hasPlugin(ANDROID_LIBRARY_PLUGIN_NAME)) {
            throw new ProjectConfigurationException("FatJar plugin must contain android library plugin", null)
        }
    }

    private static Configuration createConfiguration(Project project) {
        return project.configurations.create(CONFIGURATION_NAME)
    }

    private static void buildCompileOnlyDependencies(Project project, Configuration config) {
        project.gradle.addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies dependencies) {
                // Add compileOnly artifact for IDE
                config.dependencies.each { dependency ->
                    project.dependencies.add(COMPILE_ONLY, dependency)
                }
                project.gradle.removeListener(this)
            }

            @Override
            void afterResolve(ResolvableDependencies dependencies) { }
        })
    }

    private static List<ResolvedArtifact> findAllResolvedArtifacts(Configuration configuration) {
        List<ResolvedArtifact> resolvedArtifacts = new ArrayList<>()
        configuration.resolvedConfiguration.resolvedArtifacts.each { artifact ->
            if (artifact.type == JAR) {
                LoggingUtils.println("[Artifact] ${artifact.moduleVersion.id}")
            } else {
                LoggingUtils.println("Not supported artifact type detected : ${artifact.type}")
                throw new ProjectConfigurationException("Not supported artifact type detected : ${artifact.type}", null)
            }

            resolvedArtifacts.add(artifact)
        }
        return resolvedArtifacts
    }

    private static List<File> findAllUnResolvedFiles(Configuration configuration) {
        List<File> resolvedFiles = new ArrayList<>()
        configuration.resolvedConfiguration.files.each { file ->
            String extension = FileUtils.getFileExtension(file)
            if (extension == JAR) {
                LoggingUtils.println("[File] ${file.name}")
            } else {
                LoggingUtils.println("Not supported file type detected : $extension")
                throw new ProjectConfigurationException("Not supported file type detected : $extension", null)
            }

            resolvedFiles.add(file)
        }
        return resolvedFiles
    }
}

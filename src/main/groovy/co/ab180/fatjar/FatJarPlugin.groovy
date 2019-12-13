package co.ab180.fatjar

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.artifacts.ResolvedArtifact

class FatJarPlugin implements Plugin<Project> {

    private static final String ANDROID_LIBRARY_PLUGIN_NAME = "com.android.library"
    private static final String CONFIGURATION_NAME = "internalize"
    private static final String ARTIFACT_TYPE_JAR = "jar"
    private static final String COMPILE_ONLY = "compileOnly"

    private Configuration configuration
    private List<ResolvedArtifact> resolvedArtifacts

    @Override
    void apply(Project project) {
        LoggingUtils.init(project.logger)
        TaskUtils.init(project)
        validateAndroidPluginIncluded(project)
        configuration = createConfiguration(project)
        resolvedArtifacts = findAllResolvedArtifacts(configuration)
        project.afterEvaluate {
            project.android.libraryVariants.all { variant ->
                FatJarProcessor processor = new FatJarProcessor(variant, resolvedArtifacts, resolvedArtifacts)
                processor.process()
            }
        }
    }

    private static void validateAndroidPluginIncluded(Project project) {
        if (!project.plugins.hasPlugin(ANDROID_LIBRARY_PLUGIN_NAME)) {
            throw new ProjectConfigurationException("Fat-Jar plugin must contain android library plugin", null)
        }
    }

    private static Configuration createConfiguration(Project project) {
        Configuration config = project.configurations.create(CONFIGURATION_NAME)

        project.gradle.addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                config.dependencies.each {dependency ->
                    project.dependencies.add(COMPILE_ONLY, dependency)
                }
                project.gradle.removeListener(this)
            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) { }
        })

        return config
    }

    private static List<ResolvedArtifact> findAllResolvedArtifacts(Configuration configuration) {
        List<ResolvedArtifact> resolvedArtifacts = new ArrayList<>()
        configuration.resolvedConfiguration.resolvedArtifacts.each { artifact ->
            if (artifact.type == ARTIFACT_TYPE_JAR) {
                LoggingUtils.println("[internalize detected] ${artifact.type} - ${artifact.moduleVersion.id}")
            } else {
                throw new ProjectConfigurationException("Not supported artifact type detected : ${artifact.type}", null)
            }

            resolvedArtifacts.add(artifact)
        }
        return resolvedArtifacts
    }
}

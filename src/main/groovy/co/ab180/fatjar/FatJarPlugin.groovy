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
    private static final String EXTENSION_NAME = "repackage"
    private static final String INTERNALIZE = "internalize"
    private static final String INTERNALIZE_ALL = "internalizeAll"
    private static final String JAR = "jar"
    private static final String COMPILE_ONLY = "compileOnly"
    private static final String TEST_COMPILE_ONLY = "testCompileOnly"

    private Configuration internalizeConfiguration
    private Configuration internalizeAllConfiguration
    private RepackageExtension extension
    private List<ResolvedArtifact> resolvedArtifacts = new ArrayList<>()

    @Override
    void apply(Project project) {
        LoggingUtils.init(project.logger)
        TaskUtils.init(project)
        validateAndroidPluginIncluded(project)
        extension = createExtension(project)

        internalizeConfiguration = createConfiguration(project, INTERNALIZE, false)
        internalizeAllConfiguration = createConfiguration(project, INTERNALIZE_ALL, true)

        buildCompileOnlyDependencies(project, internalizeConfiguration)
        buildCompileOnlyDependencies(project, internalizeAllConfiguration)

        project.afterEvaluate {
            resolvedArtifacts.addAll(findAllResolvedArtifacts(internalizeConfiguration))
            resolvedArtifacts.addAll(findAllResolvedArtifacts(internalizeAllConfiguration))
            project.android.libraryVariants.all { variant ->
                FatJarProcessor processor = new FatJarProcessor(project, variant, resolvedArtifacts, extension.getRules())
                processor.process()
            }
        }
    }

    private static void validateAndroidPluginIncluded(Project project) {
        if (!project.plugins.hasPlugin(ANDROID_LIBRARY_PLUGIN_NAME)) {
            throw new ProjectConfigurationException("FatJar plugin must contain android library plugin", null)
        }
    }

    private static RepackageExtension createExtension(Project project) {
        return project.extensions.create(EXTENSION_NAME, RepackageExtension, project)
    }

    private static Configuration createConfiguration(Project project, String name, boolean transitive) {
        Configuration configuration = project.configurations.create(name)
        configuration.transitive = transitive
        return configuration
    }

    private static void buildCompileOnlyDependencies(Project project, Configuration configuration) {
        project.gradle.addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies dependencies) {
                // Add compileOnly artifact for IDE
                configuration.dependencies.each { dependency ->
                    project.dependencies.add(COMPILE_ONLY, dependency)
                    project.dependencies.add(TEST_COMPILE_ONLY, dependency)
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
}

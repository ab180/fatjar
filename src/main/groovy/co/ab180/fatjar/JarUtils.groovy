package co.ab180.fatjar

import org.gradle.api.Project
import org.zeroturnaround.zip.ZipUtil

class JarUtils {

    static void excludeMetaInfo(Project project, File jarFile) {
        File tempDirFile = File.createTempDir()
        tempDirFile.deleteDir()

        LoggingUtils.println("Excluding 'META-INF/' into '${jarFile.name}")

        project.copy {
            from project.zipTree(jarFile)
            into tempDirFile
            exclude "META-INF/"
        }

        LoggingUtils.println("Zipping '${jarFile.name}'")

        ZipUtil.pack(tempDirFile, jarFile)
    }
}

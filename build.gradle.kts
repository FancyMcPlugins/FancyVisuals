import net.minecrell.pluginyml.paper.PaperPluginDescription
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    id("java-library")

    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("io.github.goooler.shadow") version "8.1.7"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"
}

runPaper.folia.registerTask()

val supportedVersions =
    listOf("1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1")

allprojects {
    group = "de.oliver"
    val buildId = System.getenv("BUILD_ID")
    version = "0.0.0" + (if (buildId != null) ".$buildId" else "")
    description = "Simple, lightweight and fast visual plugin using packets"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://repo.fancyplugins.de/releases")
        maven(url = "https://repo.smrt-1.com/releases")
        maven(url = "https://jitpack.io")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")

    implementation(project(":api"))

    compileOnly("de.oliver:FancyLib:33") // loaded in FancyVisualLoader
    compileOnly("de.oliver:FancySitula:0.0.9") // loaded in FancyVisualLoader
    compileOnly("de.oliver.FancyAnalytics:api:0.0.8") // loaded in FancyVisualLoader
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.5") // loaded in FancyVisualLoader

    implementation("me.dave:ChatColorHandler:v2.5.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // commands
    compileOnly("org.incendo:cloud-core:2.0.0") // loaded in FancyVisualLoader
    compileOnly("org.incendo:cloud-paper:2.0.0-beta.10") // loaded in FancyVisualLoader
    compileOnly("org.incendo:cloud-annotations:2.0.0") // loaded in FancyVisualLoader
    annotationProcessor("org.incendo:cloud-annotations:2.0.0")
}

paper {
    main = "de.oliver.fancyvisuals.FancyVisuals"
    bootstrapper = "de.oliver.fancyvisuals.loaders.FancyVisualsBootstrapper"
    loader = "de.oliver.fancyvisuals.loaders.FancyVisualsLoader"
    foliaSupported = true
    version = rootProject.version.toString()
    description = "Simple, lightweight and fast visuals plugin using packets"
    apiVersion = "1.19"
    serverDependencies {
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("LuckPerms") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PermissionsEx") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion(findProperty("minecraftVersion").toString())
//        minecraftVersion("1.20.6")

        downloadPlugins {
            hangar("ViaVersion", "5.0.3")
            hangar("ViaBackwards", "5.0.3")
            hangar("PlaceholderAPI", "2.11.6")
//            modrinth("multiverse-core", "4.3.11")
        }
    }

    shadowJar {
        archiveClassifier.set("")
        dependsOn(":api:shadowJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release = 21
        // For cloud-annotations, see https://cloud.incendo.org/annotations/#command-components
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything

        val props = mapOf(
            "description" to project.description,
            "version" to project.version,
            "hash" to getCurrentCommitHash(),
            "build" to (System.getenv("BUILD_ID") ?: "").ifEmpty { "undefined" }
        )

        inputs.properties(props)

        filesMatching("paper-plugin.yml") {
            expand(props)
        }

        filesMatching("version.yml") {
            expand(props)
        }
    }
}

tasks.publishAllPublicationsToHangar {
    dependsOn("shadowJar")
}

tasks.modrinth {
    dependsOn("shadowJar")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

fun getCurrentCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "HEAD").start()
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitHash = reader.readLine()
    reader.close()
    process.waitFor()
    if (process.exitValue() == 0) {
        return commitHash ?: ""
    } else {
        throw IllegalStateException("Failed to retrieve the commit hash.")
    }
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "FancyVisuals"
        channel = "Alpha"

        apiKey.set(System.getenv("HANGAR_PUBLISH_API_TOKEN"))

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions.set(supportedVersions)
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_PUBLISH_API_TOKEN"))
    projectId.set("fancyvisuals")
    versionNumber.set(project.version.toString())
    versionType.set("alpha")
    uploadFile.set(file("build/libs/${project.name}-${project.version}.jar"))
    gameVersions.addAll(supportedVersions)
    loaders.add("paper")
}
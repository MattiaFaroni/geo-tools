import java.util.*

val geotoolsVersion: String by project
val postgresVersion: String by project

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.geocode.search"
version = "1.0.0"

repositories {
    maven { url = uri("https://repo.osgeo.org/repository/release/") }
    mavenCentral()
}

dependencies {
    implementation("org.geotools:gt-swing:$geotoolsVersion")
    implementation("org.geotools:gt-shapefile:$geotoolsVersion")
    implementation("org.geotools:gt-geojson-store:$geotoolsVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("commons-cli:commons-cli:1.8.0")
}

tasks.jar {
    manifest {
        archiveFileName.set("${project.name}-no-dependencies.jar")
    }
}

tasks.shadowJar {
    manifest {
        archiveFileName.set("${project.name}.jar")
    }
}

val generatedVersionDir = "${buildDir}/generated-version"
tasks.create("generateVersionProperties") {
    doLast {
        val propertiesFile = File("$generatedVersionDir/build.properties")
        propertiesFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", rootProject.version.toString())
        properties.setProperty("build-date", System.currentTimeMillis().toString())
        properties.store(propertiesFile.writer(), "")
    }
}

val markdownDir = "${buildDir}/markdown"
tasks.create("addMarkdownFile") {
    doLast {
        File("$rootDir/CHANGELOG.md").copyTo(File(markdownDir).resolve("CHANGELOG.md"), true)
        File("$rootDir/LICENSE.md").copyTo(File(markdownDir).resolve("LICENSE.md"), true)
        File("$rootDir/README.md").copyTo(File(markdownDir).resolve("README.md"), true)
    }
}

sourceSets {
    main {
        output.dir(generatedVersionDir, "builtBy" to "generateVersionProperties")
        output.dir(markdownDir, "builtBy" to "addMarkdownFile")
    }
}

spotless {
    java {
        target("**/*.java")
        toggleOffOn()
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

afterEvaluate{
    tasks.named("build") {
        dependsOn("spotlessApply")
    }
}
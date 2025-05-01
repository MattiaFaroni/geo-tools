import java.util.*

val geotoolsVersion: String by project
val postgresVersion: String by project
val lombokVersion: String by project
val log4jVersion: String by project

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.diffplug.spotless") version "7.0.2"
    id("io.sentry.jvm.gradle") version "5.3.0"
}

group = "com.geocode.search"
version = "1.0.2"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    maven { url = uri("https://repo.osgeo.org/repository/release/") }
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    implementation("org.geotools:gt-swing:$geotoolsVersion")
    implementation("org.geotools:gt-shapefile:$geotoolsVersion")
    implementation("org.geotools:gt-geojson-store:$geotoolsVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("commons-cli:commons-cli:1.9.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
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

val generatedVersionDir = "${layout.buildDirectory.get()}/generated-version"
tasks.register("generateVersionProperties") {
    doLast {
        val propertiesFile = File("$generatedVersionDir/build.properties")
        propertiesFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", rootProject.version.toString())
        properties.setProperty("build-date", System.currentTimeMillis().toString())
        properties.store(propertiesFile.writer(), "")
    }
}

val markdownDir = "${layout.buildDirectory.get()}/markdown"
tasks.register("addMarkdownFile") {
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
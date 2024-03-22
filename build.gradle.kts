import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    `java-library`
    application
    idea

    id("com.github.ben-manes.versions") version ("0.51.0")
    id("edu.sc.seis.launch4j") version ("3.0.5")

    kotlin("jvm") version ("1.9.23")
}

group = "dev.blocky.app.ytd"
version = "3.0.1"
description = "Downloader for YouTube videos, that extracts audio from the video."

repositories {
    mavenCentral()
    maven("https://sandec.jfrog.io/artifactory/repo")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.openjfx:javafx-base:22:win")
    implementation("org.openjfx:javafx-graphics:22:win")
    implementation("org.openjfx:javafx-controls:22:win")

    implementation("net.java.dev.jna:jna-platform:5.14.0")

    api("org.controlsfx:controlsfx:11.2.1")
    api("one.jpro.platform:jpro-mdfx:0.2.15")
    api("fr.brouillard.oss:cssfx:11.5.1")

    api("net.jthink:jaudiotagger:3.0.1")

    api("org.kohsuke:github-api:1.321")

    api("com.squareup.retrofit2:retrofit:2.10.0")
    api("com.squareup.retrofit2:converter-gson:2.10.0")

    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.squareup.okio:okio-jvm:3.9.0")

    api("org.json:json:20240303")

    api("io.github.cdimascio:dotenv-java:3.0.0")

    api("io.github.g00fy2:versioncompare:1.5.0")

    api("org.apache.commons:commons-lang3:3.14.0")
    api("commons-io:commons-io:2.15.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

application {
    mainModule.set("dev.blocky.app.ytd")
    mainClass.set("dev.blocky.app.ytd.Main")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

launch4j {
    outfile = "yt-downloader.exe"
    mainClassName = "dev.blocky.app.ytd.Main"

    outputDir = "D:\\YT-Downloader-Builds"
    libraryDir = "D:\\YT-Downloader-Builds\\lib"

    icon = "${projectDir}/src/main/resources/assets/icons/icon.ico"

    bundledJrePath = System.getenv("JAVA_HOME")
    jreMinVersion = "21"

    version = project.version.toString()
    textVersion = project.version.toString()
    copyright = "Copyright (C) 2024 BlockyDotJar (aka. Dominic R.)"
    downloadUrl = "https://www.oracle.com/java/technologies/downloads/#jdk21-windows"

    setJvmOptions(listOf("--enable-preview"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Module"] = "dev.blocky.app.ytd"
        attributes["Main-Class"] = "dev.blocky.app.ytd.Main"
        attributes["Automatic-Module-Name"] = "YouTubeDownloader"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
}

tasks.withType<JavaCompile> {
    doFirst {
        options.compilerArgs.addAll(
                arrayOf
                (
                        "--module-path", classpath.asPath,
                        "--add-modules", "javafx.base,javafx.controls,javafx.graphics",
                        "--enable-preview"
                )
        )
    }
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"

    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    outputFormatter = "plain"
    outputDir = "build/dependencyUpdates"
    reportfileName = "update-log"
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

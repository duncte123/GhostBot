/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.tasks.wrapper.Wrapper.DistributionType
import com.fasterxml.jackson.databind.ObjectMapper

plugins {
    java
    application
    idea

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        "classpath"(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.1")
    }
}

project.group = "me.duncte123"
project.version = "2.3.3"


application {
    mainClass.set("${project.group}.ghostbot.GhostBot")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()

    maven("https://maven.arbjerg.dev/releases")
    maven("https://maven.arbjerg.dev/snapshots")
    maven("https://m2.dv8tion.net/releases")
    maven("https://m2.duncte123.dev/releases")
    maven("https://duncte123.jfrog.io/artifactory/maven")
    maven("https://jitpack.io")
}

val jda = JDAVersionInfo("5.0.0-beta.15")
//val jda = JDAVersionInfo("bacd237")

dependencies {
    implementation(group = "me.duncte123", name = "botCommons", version = "3.0.16")
    implementation(group = "org.apache.commons", name = "commons-text", version = "1.10.0")
    implementation(group = "org.codehaus.groovy", name = "groovy-jsr223", version = "2.5.13")
    implementation(group = jda.group, name = "JDA", version = jda.version) {
        exclude(module = "opus-java")
    }
//    implementation(group = "dev.arbjerg", name = "lavaplayer", version = "2.0.2")
//    implementation("lavalink:local")
//    implementation(group = "com.github.FredBoat", name = "Lavalink-Client", version = "eb26770")
    implementation(group = "dev.arbjerg", name = "lavalink-client", version = "a404d235294e8bfdf575ae1e34bfbc6fc84642b9-SNAPSHOT")
//    implementation(group = "com.github.DuncteBot", name = "Lavalink-Client", version = "4f3924fb51")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.10")
    implementation(group = "net.sf.trove4j", name = "trove4j", version = "3.0.3")
}

val compileJava: JavaCompile by tasks
val shadowJar: ShadowJar by tasks
val clean: Task by tasks
val build: Task by tasks
val jar: Jar by tasks

build.apply {
    dependsOn(clean)
    dependsOn(jar)

    jar.mustRunAfter(clean)
}

val sourcesForRelease = task<Copy>("sourcesForRelease") {
    from("src/main/java") {
        include("**/Variables.java")

        val items = mapOf(
            "ghostBotVersion" to project.version
        )

        filter<ReplaceTokens>(mapOf("tokens" to items))
    }

    into("build/filteredSrc")

    includeEmptyDirs = false
}

val generateJavaSources = task<SourceTask>("generateJavaSources") {
    val javaSources = sourceSets["main"].allJava.filter {
        it.name != "Variables.java"
    }.asFileTree

    source = javaSources + fileTree(sourcesForRelease.destinationDir)

    dependsOn(sourcesForRelease)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
}

compileJava.apply {
    source = generateJavaSources.source

    dependsOn(generateJavaSources)
}

tasks.withType<Wrapper> {
    distributionType = DistributionType.BIN
    gradleVersion = "8.1.1"
}

shadowJar.apply {
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.register("generateAudioJson") {
    val output = HashMap<String, List<String>>()
    val audioFileDir = File("audioFiles")
    val listOfFiles = audioFileDir.listFiles()

    if (listOfFiles.isNullOrEmpty()) {
        throw UnsupportedOperationException("Audio list is null or empty")
    }

    listOfFiles.forEach {
        if (it.isDirectory) {
            val filesFound = arrayListOf<String>()
            it.listFiles()?.forEach { audioFile ->
                if (audioFile.isFile) {
                    filesFound.add(audioFile.name)
                }
            }

            output[it.name] = filesFound
        }
    }

    println(output)

    ObjectMapper().writerWithDefaultPrettyPrinter()
        .writeValue(File("./data/audioList.json"), output)
}

/**
 * Helper class for getting the correct JDA group
 */
class JDAVersionInfo(val version: String) {
    val group: String

    init {
        if (version.contains(".")) {
            this.group = "net.dv8tion"
        } else {
            this.group = "com.github.dv8fromtheworld"
        }
    }

}

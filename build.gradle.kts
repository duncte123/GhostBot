/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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
import java.io.ByteArrayOutputStream
import com.fasterxml.jackson.databind.ObjectMapper

plugins {
    java
    application
    idea

    id("com.github.johnrengelman.shadow") version "5.0.0"
}

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        "classpath"(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.1")
    }
}

project.group = "me.duncte123.ghostbot"
project.version = "2.0.1_${getGitHash()}"


application {
    mainClassName = "${project.group}.GhostBot"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    jcenter()

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

val jda = JDAVersionInfo("4.1.1_112")
//val jda = JDAVersionInfo("75c9b39")

dependencies {
    implementation(group = "me.duncte123", name = "botCommons", version = "1.0.73")
    implementation(group = "org.apache.commons", name = "commons-text", version = "1.6")
    implementation(group = "org.codehaus.groovy", name = "groovy-jsr223", version = "2.5.6")

    /*implementation(group = "net.dv8tion", name = "JDA", version = "4.1.1_104") {
        exclude(module = "opus-java")
    }*/

    implementation(group = jda.group, name = "JDA", version = jda.version) {
        exclude(module = "opus-java")
    }

    implementation(group = "com.sedmelluq", name = "lavaplayer", version = "1.3.32")
//    implementation("lavalink:local")
    implementation(group = "com.github.FredBoat", name = "Lavalink-Client", version = "f9ab37f")
//    implementation(group = "com.github.DuncteBot", name = "Lavalink-Client", version = "4f3924fb51")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
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
    distributionType = DistributionType.ALL
    gradleVersion = "6.1.1"
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
        .writeValue(File("audioList.json"), output)
}

fun getGitHash(): String {
    return try {
        val stdout = ByteArrayOutputStream()

        exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
            standardOutput = stdout
        }

        stdout.toString().trim()
    } catch (ignored: Throwable) {
        // Probably ramidzkh"s problem
        "DEV"
    }
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

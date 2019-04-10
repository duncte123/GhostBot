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
import java.io.ByteArrayOutputStream

plugins {
    java
    application
    idea

    id("com.github.johnrengelman.shadow") version "5.0.0"
}

project.group = "me.duncte123.ghostbot"
project.version = "2.0.1_${getGitHash()}"


application {
    mainClassName = "${project.group}.GhostBot"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_10
    targetCompatibility = JavaVersion.VERSION_1_10
}

repositories {
    jcenter()

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    //My little utils
    implementation(group = "com.github.duncte123", name = "botCommons", version = "2e43820")
    
    implementation(group = "org.codehaus.groovy", name = "groovy-jsr223", version = "2.5.6")

    //JDA
    implementation(group = "net.dv8tion", name = "JDA", version = "3.8.3_462") {
        exclude(module = "opus-java")
    }
    //LavaPlayer/Lavalink
    implementation(group = "com.sedmelluq", name = "lavaplayer", version = "1.3.16")
    implementation(group = "com.github.FredBoat", name = "Lavalink-Client", version = "1a4b0f5")

    // Logback classic
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")

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
    gradleVersion = "5.2.1"
}

shadowJar.apply {
    archiveClassifier.set("")
    archiveVersion.set("")
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

import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm") version "1.8.0"
    id("java-library")
    id("com.github.SteveTheEngineer.SS-BukkitGradle") version "1.6"
    `maven-publish`
}

group = "me.ste.stevesseries"
version = "0.0.0-mc1.19.3"
description = "A collection of utilities for Steve's Series plugins. (and others too!)"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
}

allprojects {
    apply<KotlinPluginWrapper>()
    apply<MavenPublishPlugin>()

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    }

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
    }
}

dependencies {
    softDepend("com.github.SteveTheEngineer:SS-Kotlin:1.8.0")

    api(project(":API"))
}

tasks {
    jar {
        from(
            project(":API").sourceSets.main.get().output,
            project(":Compat").sourceSets.main.get().output
        )
    }
}

runServer {
    downloadUri.set("https://api.papermc.io/v2/projects/paper/versions/1.19.3/builds/448/downloads/paper-1.19.3-448.jar")
}

pluginDescription {
    mainClass.set("me.ste.stevesseries.base.Base")
    apiVersion.set("1.16")
    authors.add("SteveTheEngineer")
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            artifactId = "base"
            group = "com.github.SteveTheEngineer.SS-Base"

            from(components.getByName("java"))
        }
    }
}

import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.4.30"
}

group = "me.ste.stevesseries"
version = System.getenv("BUILD_NUMBER") ?: 0

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://repo.steene.cf/repository/generic/")
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.processResources {
    from(sourceSets.main.get().resources.srcDirs) {
        include("plugin.yml")
        filter<ReplaceTokens>("tokens" to hashMapOf("version" to version))
    }
}
rootProject.name = "SS-Base"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://jitpack.io")
    }
}

include("API", "Compat")
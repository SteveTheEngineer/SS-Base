group = "me.ste.stevesseries.base"
version = rootProject.version

dependencies {
    api("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("api") {
            artifactId = "base-api"
            group = "com.github.SteveTheEngineer.SS-Base"

            from(components.getByName("java"))
        }
    }
}
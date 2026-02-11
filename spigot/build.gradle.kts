plugins {
    id("com.gradleup.shadow")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

repositories {
    maven {
        name = "spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(project(":common"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.apache.logging.log4j:log4j-core:2.17.1")
}

tasks.shadowJar {
    archiveBaseName.set("WebConsoleServer-Spigot")
    archiveClassifier.set("")
    relocate("org.java_websocket", "fr.maxlego08.console.libs.websocket")
    destinationDirectory.set(file("${rootProject.projectDir}/target"))
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

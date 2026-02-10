plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "fr.maxlego08.console"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("org.java_websocket", "fr.maxlego08.console.libs.websocket")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
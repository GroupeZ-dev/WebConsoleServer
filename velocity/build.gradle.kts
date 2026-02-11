plugins {
    id("com.gradleup.shadow")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    implementation(project(":common"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}

tasks.shadowJar {
    archiveBaseName.set("WebConsoleServer-Velocity")
    archiveClassifier.set("")
    relocate("org.java_websocket", "fr.maxlego08.console.libs.websocket")
    destinationDirectory.set(file("${rootProject.projectDir}/target"))
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

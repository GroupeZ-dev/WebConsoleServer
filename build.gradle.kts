plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4" apply false
}

allprojects {
    group = "fr.maxlego08.console"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    repositories {
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

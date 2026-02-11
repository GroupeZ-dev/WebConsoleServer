plugins {
    id("java-library")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

dependencies {
    api("org.java-websocket:Java-WebSocket:1.5.3")
}

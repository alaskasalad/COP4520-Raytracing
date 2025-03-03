plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Utils
    implementation("io.freefair.gradle:lombok-plugin:8.6")
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("net.kyori:indra-git:3.1.1")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}
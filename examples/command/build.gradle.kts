plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

application {
    mainClass = "starry.adventure.example.command.CommandKt"
}

group = "starry.adventure.examples.command"

repositories {
    mavenCentral()

    maven("https://libraries.minecraft.net")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    api(project(":brigadier"))
    api(project(":parser"))

    implementation(libs.jline)
    implementation(libs.jansi)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
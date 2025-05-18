plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

    id("maven-publish")
}

group = "starry.adventure.brigadier"

repositories {
    mavenCentral()

    maven("https://libraries.minecraft.net")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))

    api(libs.brigadier)
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.coroutines)

    compileOnly(project(":parser"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components.getByName("kotlin"))
    }
}

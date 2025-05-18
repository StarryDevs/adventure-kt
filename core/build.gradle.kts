plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

    id("maven-publish")
}

group = "starry.adventure.core"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.coroutines)
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

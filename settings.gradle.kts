plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "adventure-kt"

include(
    "core",
    "brigadier", "dfu", "event", "registry",
    "parser",
    "examples", "examples:command"
)
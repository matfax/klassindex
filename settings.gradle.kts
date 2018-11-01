pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

rootProject.name = "klassindex"

include("library", "processor", "test")

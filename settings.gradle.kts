pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("http://dl.bintray.com/kotlin/kotlin-eap")
    }
}

rootProject.name = "klassindex"

include("library", "processor", "test")

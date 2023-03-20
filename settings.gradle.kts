pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "klassindex"

plugins {
    id("com.gradle.enterprise").version("3.12.5")
}

include("library", "processor", "test")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
    }
}

plugins {
    base
    kotlin("jvm") version "1.8.10" apply false
    kotlin("kapt") version "1.8.10" apply false
    id("com.palantir.git-version") version "2.0.0"
}

allprojects {
    apply(plugin = "com.palantir.git-version")

    repositories {
        mavenCentral()
        google()
    }

    group = "com.github.matfax.klassindex"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.8"
            apiVersion = "1.8"
        }
    }
}

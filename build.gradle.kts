plugins {
    base
    kotlin("jvm") version "1.8.20" apply false
    kotlin("kapt") version "1.8.20" apply false
    id("org.jetbrains.dokka") version "1.8.10" apply false
    id("com.vanniktech.maven.publish") version "0.25.1"
    id("com.palantir.git-version") version "3.0.0"
}

allprojects {
    apply(plugin = "com.palantir.git-version")

    repositories {
        mavenCentral()
        google()
    }

    val gitVersion: groovy.lang.Closure<String> by extra
    version = gitVersion()
    group = "fyi.fax.klassindex"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.8"
            apiVersion = "1.8"
        }
    }
}

val publishConfig = closureOf<Project> {
    apply(plugin = "com.vanniktech.maven.publish")

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/matfax/klassindex")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

project("processor", publishConfig)
project("library", publishConfig)

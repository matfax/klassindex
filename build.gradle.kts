plugins {
    base
    kotlin("jvm") version "1.8.10" apply false
    kotlin("kapt") version "1.8.10" apply false
    id("org.jetbrains.dokka") version "1.8.10" apply false
    id("com.vanniktech.maven.publish") version "0.24.0"
    id("com.palantir.git-version") version "2.0.0"
}

allprojects {
    apply(plugin = "com.palantir.git-version")

    repositories {
        mavenCentral()
        google()
    }

    val gitVersion: groovy.lang.Closure<String> by extra
    version = gitVersion()
    group = "com.github.matfax.klassindex"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
            languageVersion = "1.8"
            apiVersion = "1.8"
        }
    }
}

project("processor") {

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

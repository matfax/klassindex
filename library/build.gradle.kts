plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.8.10"
    id("com.vanniktech.maven.publish") version "0.24.0"
}

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
}

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

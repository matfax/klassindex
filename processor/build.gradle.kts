plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka") version "1.8.10"
    id("com.vanniktech.maven.publish") version "0.24.0"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":library"))
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.google.auto.service:auto-service:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/matfax/klassindex")
            credentials(PasswordCredentials::class)
        }
    }
}

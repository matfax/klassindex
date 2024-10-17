plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":library"))
    implementation("com.squareup:kotlinpoet:1.18.1")
    implementation("com.google.auto.service:auto-service:1.1.1")
    kapt("com.google.auto.service:auto-service:1.1.1")
}

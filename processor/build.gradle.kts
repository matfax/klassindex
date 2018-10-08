plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    compile(kotlin("stdlib"))
    compile(project(":library"))
    compile("com.github.matfax:kotlinpoet:kotlinpoet-1.0.0-RC1")
    compile("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
}

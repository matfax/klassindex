plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
}

mavenPublishing {
    coordinates("$group", "library", "$version")
}

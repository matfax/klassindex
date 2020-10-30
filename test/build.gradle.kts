plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":library"))
    kapt(project(":processor"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.23")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

kapt {
    useBuildCache = true
    arguments {
        arg(
                "com.github.matfax.klassindex.IndexAnnotated",
                "$group.GivenAnnotation",
                "$group.AnotherGivenAnnotation"
        )
        arg(
                "com.github.matfax.klassindex.IndexSubclasses",
                "$group.GivenAbstractKlass",
                "java.lang.Exception"
        )
    }
}

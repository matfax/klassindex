plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":library"))
    kapt(project(":processor"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
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
                "$group.IndexAnnotated",
                "$group.GivenAnnotation",
                "$group.AnotherGivenAnnotation"
        )
        arg(
                "$group.IndexSubclasses",
                "$group.GivenAbstractKlass",
                "java.lang.Exception"
        )
    }
}

publishing {}

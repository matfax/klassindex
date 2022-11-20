import groovy.lang.Closure

plugins {
    base
    maven
    kotlin("jvm") version "1.7.21" apply false
    kotlin("kapt") version "1.7.21" apply false
    id("com.palantir.git-version") version "0.12.3"
}

allprojects {
    apply(plugin = "com.palantir.git-version")
    apply(plugin = "maven")

    repositories {
        mavenCentral()
        google()
    }

    group = "com.github.matfax.klassindex"
    version = (extensions.extraProperties.get("gitVersion") as? Closure<*>)?.call() ?: "dirty"

    tasks.create("printVersionName") {
        doLast { println(version) }
    }

    tasks {
        getByName<Upload>("uploadArchives") {

            repositories {

                withConvention(MavenRepositoryHandlerConvention::class) {

                    mavenDeployer {

                        withGroovyBuilder {
                            "repository"("url" to uri("$buildDir/m2/releases"))
                            "snapshotRepository"("url" to uri("$buildDir/m2/snapshots"))
                        }

                        pom.project {
                            withGroovyBuilder {
                                "parent" {
                                    "groupId"("org.gradle")
                                    "artifactId"("kotlin-dsl")
                                    "version"("1.0")
                                }
                                "licenses" {
                                    "license" {
                                        "name"("The Apache Software License, Version 2.0")
                                        "url"("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                        "distribution"("repo")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

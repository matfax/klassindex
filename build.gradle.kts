import com.gradle.scan.plugin.BuildScanExtension
import groovy.lang.Closure
import org.gradle.internal.scan.config.BuildScanConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.sure
import java.lang.reflect.Method

plugins {
    base
    maven
    kotlin("jvm") version "1.3.0-rc-131" apply false
    kotlin("kapt") version "1.3.0-rc-131" apply false
    id("com.palantir.git-version") version "0.12.0-rc2"
    id("com.gradle.build-scan") version "1.16"
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        println("Configuring $name in project ${project.name}...")
        kotlinOptions {
            suppressWarnings = true
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs.plus("-XXLanguage:+InlineClasses")
        }
    }
}

allprojects {
    apply(plugin = "com.palantir.git-version")
    apply(plugin = "maven")

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("http://dl.bintray.com/kotlin/kotlin-eap")
    }

    group = "com.github.matfax"
    version = (extensions.extraProperties.get("gitVersion") as Closure<*>).call() ?: "dirty"

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

configure<BuildScanExtension> {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
}

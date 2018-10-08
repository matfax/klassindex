import com.gradle.scan.plugin.BuildScanExtension
import groovy.lang.Closure
import org.gradle.internal.scan.config.BuildScanConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.sure
import java.lang.reflect.Method

plugins {
    base
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

    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
        maven("http://dl.bintray.com/kotlin/kotlin-eap")
    }

    group = "com.github.matfax"
    version = (extensions.extraProperties.get("gitVersion") as Closure<*>).call() ?: "dirty"
}

configure<BuildScanExtension> {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
}

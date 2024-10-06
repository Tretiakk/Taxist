buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle.v852)
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin.v170)
    }
}

plugins {
    alias(libs.plugins.compose.compiler) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

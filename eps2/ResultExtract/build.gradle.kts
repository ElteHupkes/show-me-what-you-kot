import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"

    // Iport JSON serialization
    kotlin("plugin.serialization") version "1.5.31"
    application
}

group = "me.elte"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
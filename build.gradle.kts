import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.4.30"
}

group = "dev.basshelal"
version = "1.0"

val javaVersion = JavaVersion.VERSION_1_8

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.jnr:jnr-ffi:2.2.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

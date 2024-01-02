plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "io.github.koxx12dev"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

tasks.getByName("build") {
    dependsOn("shadowJar")
}

dependencies {
    implementation("org.ow2.asm:asm-tree:9.3")
}

//modify manifest
tasks.getByName<Jar>("jar") {
    manifest.attributes(
        mapOf(
            "Can-Retransform-Classes" to true,
            "Premain-Class" to "io.github.koxx12dev.Agent",
            "Agent-Class" to "io.github.koxx12dev.Agent"
        )
    )
}
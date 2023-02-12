plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "2.2.3"
    implementation("org.telegram:telegrambots:6.4.0")
    //serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
}

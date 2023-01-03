@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    id("maven-publish")
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}

group = "com.thoughtworks.ark.router"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
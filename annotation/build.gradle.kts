@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

group = "com.thoughtworks.ark.router"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
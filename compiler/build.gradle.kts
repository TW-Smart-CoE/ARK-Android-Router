@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kapt)
    id("maven-publish")
}

group = "com.thoughtworks.ark.router"

dependencies {
    implementation(project(":annotation"))
    implementation(libs.kotlin.poet)

    implementation(libs.auto.service)
    kapt(libs.auto.service)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
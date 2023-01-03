@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.detekt)
    id("maven-publish")
}

group = "com.thoughtworks.ark.router"

dependencies {
    implementation(project(":annotation"))
    implementation(libs.kotlin.poet)

    implementation(libs.auto.service)
    kapt(libs.auto.service)

    detektPlugins(libs.detekt.formatting)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
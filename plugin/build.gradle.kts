plugins {
    id("kotlin")
    id("groovy")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.1.0"
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.android.tools.build:gradle:7.0.4")
    implementation("com.android.tools.build:gradle-api:7.0.4")
}

group = "io.github.thoughtworks.ark"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("routerPlugin") {
            id = "io.github.thoughtworks.ark.router"
            displayName = "Router plugin"
            description = "Router plugin"
            implementationClass = "zlc.season.butterfly.plugin.ButterflyPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.thoughtworks.ark"
            artifactId = "io.github.thoughtworks.ark.router"
            version = "1.0.0"

            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}
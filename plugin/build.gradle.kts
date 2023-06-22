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
    implementation("com.android.tools.build:gradle-api:7.4.2")
}

group = "io.github.ssseasonnn.router"
version = "1.0.0"

tasks.withType(Copy::class.java).all {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

gradlePlugin {
    plugins {
        create("routerPlugin") {
            id = "io.github.ssseasonnn.router"
            displayName = "Router Plugin"
            description = "An Android navigation framework plugin, to help user auto register RouterTable."
            implementationClass = "com.thoughtworks.ark.router.plugin.RouterPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/TW-Smart-CoE/ARK-Android-Router"
    vcsUrl = "https://github.com/TW-Smart-CoE/ARK-Android-Router.git"
    tags = listOf("Router")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.thoughtworks.ark.router"
            artifactId = "com.thoughtworks.ark.router"
            version = "1.0.0"

            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}
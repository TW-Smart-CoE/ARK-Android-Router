@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.parcelize)
    id("maven-publish")
}

android {
    setCompileSdkVersion(libs.versions.sdk.compile.version.get().toInt())

    defaultConfig {
        minSdk = libs.versions.sdk.min.version.get().toInt()
        targetSdk = libs.versions.sdk.target.version.get().toInt()
        vectorDrawables { useSupportLibrary = true }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    buildFeatures.compose = true

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.toString()
    }
}

dependencies {
    api(project(":annotation"))
    api(libs.clarity)
    api(libs.kotlin.coroutines)
    api(libs.lifecycle.runtime.ktx)
    api(libs.fragment)
    api(libs.core.ktx)
    api(libs.compose.ui)
    api(libs.compose.runtime)

    testImplementation(libs.junit4)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
            }
        }
    }
}
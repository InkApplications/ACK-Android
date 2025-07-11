plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("PacketDatabase") {
            packageName.set("com.inkapplications.ack.data")
        }
    }
}

android {
    namespace = "com.inkapplications.ack.data"
    compileSdk = 34
    ndkVersion = "21.3.6528147"

    defaultConfig {
        minSdk = 14
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/src/main/schema")
            }
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDirs("$projectDir/src/main/schema")
    }

    externalNativeBuild {
        cmake {
            version = "3.31.6"
            path = File("${projectDir}/src/main/cpp/CMakeLists.txt")
        }
    }
    packaging {
        resources {
            excludes += "META-INF/kotlinx-coroutines-core.kotlin_module"
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    api(libs.coroutines.core)

    implementation(projects.androidExtensions)

    api(libs.ack.codec)
    api(libs.ack.client)
    api(libs.ack.structures)

    api(libs.kimchi.logger)

    implementation(libs.watermelon.coroutines)
    implementation(libs.watermelon.standard)
    implementation(libs.spondee.units)

    implementation(libs.bundles.sqldelight.android)
    implementation(libs.bundles.dagger.libraries)
    kapt(libs.bundles.dagger.kapt)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.core)
}

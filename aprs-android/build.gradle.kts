plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 31
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
            version = "3.10.2"
            path = File("${projectDir}/src/main/cpp/CMakeLists.txt")
        }
    }
    packagingOptions {
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
    }
}

dependencies {
    api(kotlinLibraries.coroutines.core)

    kapt(androidLibraries.androidx.room.compiler)
    implementation(androidLibraries.androidx.room.runtime)
    implementation(androidLibraries.androidx.room.ktx)

    api(inkLibraries.ack.codec)
    api(inkLibraries.ack.client)
    api(inkLibraries.ack.structures)

    api(inkLibraries.kimchi.logger)

    implementation(inkLibraries.watermelon.coroutines)
    implementation(inkLibraries.spondee.units)

    implementation(thirdParty.dagger.core)
    kapt(thirdParty.dagger.compiler)

    androidTestImplementation(androidLibraries.androidx.test.runner)
    androidTestImplementation(androidLibraries.androidx.test.core)
    androidTestImplementation(androidLibraries.androidx.room.testing)
}

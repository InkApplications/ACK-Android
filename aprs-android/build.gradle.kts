plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    ndkVersion = "21.3.6528147"

    defaultConfig {
        minSdkVersion(8)
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/src/main/schema")
            }
        }
    }

    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
    externalNativeBuild {
        cmake {
            version = "3.10.2"
            path = File("${projectDir}/src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(projects.kotlinExtensions)
    api(libraries.coroutines.core)

    implementation(libraries.karps.parser)
    implementation(libraries.karps.client)
    api(libraries.karps.structures)

    api(libraries.kimchi.logger)

    implementation(libraries.dagger.core)
    kapt(libraries.dagger.compiler)

    implementation(ThreeTenBp.noTzDb)

    implementation(libraries.androidx.room.runtime)
    implementation(libraries.androidx.room.ktx)
    kapt(libraries.androidx.room.compiler)
}

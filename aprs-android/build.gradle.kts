plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    ndkVersion = "21.3.6528147"

    defaultConfig {
        minSdkVersion(5)
    }

    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
    externalNativeBuild {
        cmake {
            version = "3.10.2"
            setPath("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    api(project(":aprs-data"))
    implementation(kotlin("stdlib"))
    api(Coroutines.core)

    implementation("com.github.ab0oo:javAPRSlib:859c87ffa5")

    implementation(Kimchi.logger)

    implementation(Dagger.runtime)
    kapt(Dagger.compiler)

    implementation(ThreeTenBp.noTzDb)

    implementation(Room.runtime)
    implementation(Room.ktx)
    kapt(Room.compiler)
}
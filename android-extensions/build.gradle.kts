plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(14)
    }

    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
}

dependencies {
    implementation(libraries.coroutines.core)
    api(libraries.androidx.core)
    implementation(libraries.androidx.preference)
    api(libraries.dagger.core)
    kapt(libraries.dagger.compiler)
}

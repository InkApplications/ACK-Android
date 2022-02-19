plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(21)
    }

    lintOptions {
        tasks.lint {
            enabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerVersion = libraries.versions.kotlin.get()
        kotlinCompilerExtensionVersion = libraries.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(libraries.coroutines.core)

    api(libraries.androidx.core)
    api(libraries.androidx.compose.ui)
    api(libraries.androidx.compose.foundation)
    implementation(libraries.androidx.preference)

    api(libraries.ack.structures)

    api(libraries.dagger.core)
    kapt(libraries.dagger.compiler)
}

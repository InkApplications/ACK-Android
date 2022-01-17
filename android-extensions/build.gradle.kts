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
        useIR = true
    }

    composeOptions {
        kotlinCompilerVersion = "1.5.10"
        kotlinCompilerExtensionVersion = "1.0.0-rc02"
    }
}

dependencies {
    implementation(libraries.coroutines.core)

    api(libraries.androidx.core)
    api(libraries.androidx.compose.ui)
    api(libraries.androidx.compose.foundation)
    implementation(libraries.androidx.preference)

    api(libraries.karps.structures)

    api(libraries.dagger.core)
    kapt(libraries.dagger.compiler)
}

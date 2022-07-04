plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
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

    testImplementation(libraries.junit)
    testImplementation(libraries.coroutines.test)
    testImplementation(libraries.kotlin.test.core)
}

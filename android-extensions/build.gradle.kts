plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.inkapplications.android.extensions"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_11
        sourceCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.coroutines.core)

    api(libs.androidx.core)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)
    implementation(libs.androidx.preference)

    api(libs.ack.structures)

    implementation(libs.bundles.dagger.libraries)
    kapt(libs.bundles.dagger.kapt)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.kotlin.test.core)
}

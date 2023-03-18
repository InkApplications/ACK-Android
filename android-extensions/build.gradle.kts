plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(kotlinLibraries.coroutines.core)

    api(androidLibraries.androidx.core)
    api(androidLibraries.androidx.compose.ui)
    api(androidLibraries.androidx.compose.foundation)
    implementation(androidLibraries.androidx.preference)

    api(inkLibraries.ack.structures)

    implementation(thirdParty.bundles.dagger.libraries)
    kapt(thirdParty.bundles.dagger.kapt)

    testImplementation(thirdParty.junit)
    testImplementation(kotlinLibraries.coroutines.test)
    testImplementation(kotlinLibraries.kotlin.test.core)
}

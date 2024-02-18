plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.inkapplications.ack.android.mapbox"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", optionalStringProperty("mapboxPublic").buildQuote())
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(projects.maps)
    implementation(projects.aprsAndroid)
    implementation(libs.androidx.annotation)
    api(libs.androidx.compose.foundation)
    implementation(libs.mapbox.android.sdk)
    implementation(libs.watermelon.android)
}

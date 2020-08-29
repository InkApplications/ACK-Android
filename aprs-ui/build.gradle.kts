plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(29)
    ndkVersion = "21.3.6528147"
    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(29)
        multiDexEnabled = true
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${stringProperty("mapbox.public", "")}\"")
        versionCode = intProperty("versionCode", 1)
        versionName = stringProperty("versionName", "SNAPSHOT")
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }
    }
    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    androidExtensions {
        isExperimental = true
    }
}

dependencies {
    implementation(project(":kotlin-extensions"))
    implementation(project(":android-extensions"))
    implementation(project(":aprs-android"))

    implementation(kotlin("stdlib"))
    implementation(Coroutines.android)

    implementation(AndroidX.AppCompat.core)
    implementation(AndroidX.ConstraintLayout.core)
    implementation(AndroidX.RecyclerView.core)
    implementation(AndroidX.Preference.core)
    implementation(AndroidX.Preference.ktx)

    implementation(Google.Material.core)

    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:9.3.0")
    implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0")

    implementation(Groupie.core)
    implementation(Groupie.extensions)

    implementation(Dagger.runtime)
    kapt(Dagger.compiler)

    implementation(Kimchi.static)

    implementation("com.google.firebase:firebase-config-ktx:19.2.0")
    implementation("com.google.firebase:firebase-analytics-ktx:17.5.0")

    testImplementation(JUnit.core)
    testImplementation(Coroutines.test)
    testImplementation(kotlin("test"))
}

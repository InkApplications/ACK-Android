plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdkVersion(31)
    ndkVersion = "21.3.6528147"
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(31)
        multiDexEnabled = true
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${stringProperty("mapbox.public", "")}\"")
        versionCode = intProperty("versionCode", 1)
        versionName = stringProperty("versionName", "SNAPSHOT")
    }
    buildFeatures {
        compose = true
    }

    signingConfigs {
        create("parameterSigning") {
            storeFile = project.properties.getOrDefault("signingFile", null)
                ?.toString()
                ?.let { File("${project.rootDir}/$it") }
            keyAlias = project.properties.getOrDefault("signingAlias", null)?.toString()
            keyPassword = project.properties.getOrDefault("signingKeyPassword", null)?.toString()
            storePassword = project.properties.getOrDefault("signingStorePassword", null)?.toString()
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = if (project.hasProperty("signingFile")) {
                signingConfigs.getByName("parameterSigning")
            } else {
                signingConfigs.getByName("debug")
            }

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

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    composeOptions {
        kotlinCompilerVersion = "1.5.10"
        kotlinCompilerExtensionVersion = "1.0.0-rc02"
    }

    androidExtensions {
        isExperimental = true
    }
    packagingOptions {
        exclude("META-INF/core.kotlin_module")
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.kotlinExtensions)
    implementation(projects.androidExtensions)
    implementation(projects.aprsAndroid)

    implementation(libraries.coroutines.android)

    implementation(libraries.androidx.appcompat)
    implementation(libraries.androidx.constraintlayout)
    implementation(libraries.androidx.recyclerview)
    implementation(libraries.androidx.preference)
    implementation(libraries.androidx.activity.ktx)
    implementation(libraries.bundles.androidx.compose.full)

    implementation(libraries.material.core)

    implementation("com.mapbox.mapboxsdk:mapbox-android-sdk:9.3.0")
    implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0")

    implementation(libraries.groupie.core)
    implementation(libraries.groupie.extensions)

    implementation(libraries.dagger.core)
    kapt(libraries.dagger.compiler)

    implementation(libraries.kimchi.core)
    implementation(libraries.kimchi.firebase.analytics)
    implementation(libraries.kimchi.firebase.crashlytics)

    // Force latest local spondee snapshot for development.
    // TODO: Remove before release.
    api("com.inkapplications.spondee:measures:1.0-SNAPSHOT")
    implementation(libraries.karps.client)

    implementation(libraries.watermelon)

    implementation("com.google.firebase:firebase-config-ktx:19.2.0")
    implementation("com.google.firebase:firebase-analytics-ktx:17.5.0")

    testImplementation(libraries.junit)
    testImplementation(libraries.coroutines.test)
    testImplementation(libraries.kotlin.test.core)
}
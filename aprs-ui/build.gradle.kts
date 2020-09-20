plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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
    androidExtensions {
        isExperimental = true
    }
    testOptions {
        unitTests {
            it.isReturnDefaultValues = true
        }
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
    implementation(Kimchi.Firebase.analytics)
    implementation(Kimchi.Firebase.crashlytics)

    implementation(Watermelon.coroutines)

    implementation("com.google.firebase:firebase-config-ktx:19.2.0")
    implementation("com.google.firebase:firebase-analytics-ktx:17.5.0")

    testImplementation(JUnit.core)
    testImplementation(Coroutines.test)
    testImplementation(kotlin("test"))
}

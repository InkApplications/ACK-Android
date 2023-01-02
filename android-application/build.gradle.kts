plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services") apply false
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.firebase.crashlytics")
}

val useGoogleServices = project.file("google-services.json").exists()

if (useGoogleServices) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    compileSdk = 31
    ndkVersion = "21.3.6528147"
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        multiDexEnabled = true
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${stringProperty("mapboxPublic", "")}\"")
        buildConfigField("boolean", "USE_GOOGLE_SERVICES", useGoogleServices.toString())
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
        getByName("debug") {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ack: APRS*")
        }
        getByName("release") {
            resValue("string", "app_name", "Ack: APRS")
            applicationIdSuffix = if (project.booleanProperty("snapshot", false)) ".snapshot" else null
            signingConfig = if (project.hasProperty("signingFile")) {
                signingConfigs.getByName("parameterSigning")
            } else {
                signingConfigs.getByName("debug")
            }

            isMinifyEnabled = false
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = androidLibraries.versions.compose.compiler.get()
    }

    packagingOptions {
        exclude("META-INF/core.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.androidExtensions)
    implementation(projects.aprsAndroid)

    implementation(kotlinLibraries.coroutines.android)

    implementation(androidLibraries.androidx.appcompat)
    implementation(androidLibraries.androidx.constraintlayout)
    implementation(androidLibraries.androidx.recyclerview)
    implementation(androidLibraries.androidx.preference)
    implementation(androidLibraries.androidx.activity.ktx)
    implementation(androidLibraries.bundles.androidx.compose.full)

    implementation(thirdParty.google.material.core)

    implementation(thirdParty.mapbox.android.sdk)

    implementation(thirdParty.dagger.core)
    kapt(thirdParty.dagger.compiler)

    implementation(thirdParty.google.license.core)

    implementation(inkLibraries.kimchi.core)
    implementation(inkLibraries.kimchi.firebase.analytics)
    implementation(inkLibraries.kimchi.firebase.crashlytics)

    api(inkLibraries.spondee.units)
    implementation(inkLibraries.ack.client)

    implementation(inkLibraries.watermelon.coroutines)
    implementation(inkLibraries.watermelon.standard)
    implementation(inkLibraries.watermelon.android)

    implementation(thirdParty.firebase.config)
    implementation(thirdParty.firebase.analytics)

    testImplementation(thirdParty.junit)
    testImplementation(kotlinLibraries.coroutines.test)
    testImplementation(kotlinLibraries.kotlin.test.core)
}

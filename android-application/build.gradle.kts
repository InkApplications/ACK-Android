plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services") apply false
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
}

val useGoogleServices = project.file("google-services.json").exists()

if (useGoogleServices) {
    apply(plugin = "com.google.gms.google-services")
}

android {
    compileSdk = 34
    namespace = "com.inkapplications.ack.android"
    defaultConfig {
        minSdk = 21
        targetSdk = 34
        multiDexEnabled = true
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", optionalStringProperty("mapboxPublic").buildQuote())
        buildConfigField("boolean", "USE_GOOGLE_SERVICES", useGoogleServices.toString())
        buildConfigField("String", "COMMIT", optionalStringProperty("commit").buildQuote())
        versionCode = intProperty("versionCode", 1)
        versionName = stringProperty("versionName", "SNAPSHOT")
        javaCompileOptions.annotationProcessorOptions.arguments["dagger.hilt.disableModulesHaveInstallInCheck"] = "true"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" +
             "${project.projectDir.absolutePath}/compose_compiler_config.conf"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "META-INF/core.kotlin_module"
            excludes += "META-INF/kotlinx-coroutines-core.kotlin_module"
        }
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

    implementation(libs.coroutines.android)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.bundles.androidx.compose.full)

    implementation(libs.google.material.core)

    implementation(libs.mapbox.android.sdk)

    implementation(libs.bundles.dagger.libraries)
    kapt(libs.bundles.dagger.kapt)

    implementation(libs.google.license.core)

    implementation(libs.kimchi.core)
    implementation(libs.kimchi.firebase.analytics)
    implementation(libs.kimchi.firebase.crashlytics)

    api(libs.spondee.units)
    implementation(libs.ack.client)

    implementation(libs.bundles.watermelon)

    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.kotlin.test.core)
    coreLibraryDesugaring(libs.android.tools.desugar.jdk)
}

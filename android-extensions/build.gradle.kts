plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(14)
    }

    lintOptions {
        tasks.lint {
            enabled = false
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(Coroutines.core)

    api(AndroidX.Ktx.core)
    implementation(AndroidX.Preference.core)
    api(Dagger.runtime)
    kapt(Dagger.compiler)
}

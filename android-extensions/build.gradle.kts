plugins {
    id("com.android.library")
    kotlin("android")
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

//    api(AndroidX.Ktx.core)
}

plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}
dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.android.gradle)
    implementation(libs.google.services)
    implementation(libs.google.license.plugin)
    implementation(libs.firebase.crashlytics.gradle)
    implementation(libs.hilt.plugin)
    implementation(libs.sqldelight.gradle)
}

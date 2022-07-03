plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}
dependencies {
    implementation(libraries.kotlin.gradle)
    implementation(libraries.kotlin.gradle)
    implementation(libraries.android.gradle)
    implementation(libraries.google.services)
    implementation(libraries.google.license.plugin)
    implementation(libraries.firebase.crashlytics.gradle)
}

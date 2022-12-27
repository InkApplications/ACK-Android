plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}
dependencies {
    implementation(kotlinLibraries.kotlin.gradle)
    implementation(androidLibraries.android.gradle)
    implementation(thirdParty.google.services)
    implementation(thirdParty.google.license.plugin)
    implementation(thirdParty.firebase.crashlytics.gradle)
}

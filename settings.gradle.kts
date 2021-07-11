enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "aprs"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            from(files(
                "gradle/android.versions.toml",
                "gradle/google.versions.toml",
                "gradle/ink.versions.toml",
                "gradle/kotlin.versions.toml",
                "gradle/libraries.versions.toml"
            ))
        }
    }
}

include("android-extensions")
include("aprs-android")
include("aprs-ui")
include("kotlin-extensions")

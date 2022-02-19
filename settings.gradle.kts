enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "ack-android"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            from(fileTree("gradle/versions").matching {
                include("*.toml")
            })
        }
    }
}

include("android-extensions")
include("aprs-android")
include("android-application")

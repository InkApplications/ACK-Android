enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            from(fileTree("../gradle/versions").matching {
                include("*.toml")
            })
        }
    }
}

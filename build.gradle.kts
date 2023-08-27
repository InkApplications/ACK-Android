import org.gradle.api.tasks.testing.logging.TestExceptionFormat

gradle.startParameter.excludedTaskNames.add("lint")
subprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://jitpack.io")
        maven(url = "https://api.mapbox.com/downloads/v2/releases/maven") {
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials.username = "mapbox"
            credentials.password = stringProperty("mapboxPrivate", "")
        }
        mavenLocal()
    }

    tasks.withType(Test::class) {
        testLogging.exceptionFormat = TestExceptionFormat.FULL
    }
    afterEvaluate {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KaptGenerateStubs> {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }
}


object AndroidX {
    object Ktx {
        const val version = "1.3.0"
        const val group = "androidx.core"

        const val core = "$group:core-ktx:$version"
    }

    object AppCompat {
        const val version = "1.1.0"
        const val group = "androidx.appcompat"

        const val core = "$group:appcompat:$version"
    }

    object RecyclerView {
        const val version = "1.1.0"
        const val group = "androidx.recyclerview"

        const val core = "$group:recyclerview:$version"
    }
    object ConstraintLayout {
        const val version = "1.1.3"
        const val group = "androidx.constraintlayout"

        const val core = "$group:constraintlayout:$version"
    }

    object Preference {
        const val group = "androidx.preference"
        const val version = "1.1.1"

        const val core = "$group:preference:$version"
        const val ktx = "$group:preference-ktx:$version"
    }
}

object Google {
    object Material {
        const val version = "1.1.0"
        const val group = "com.google.android.material"

        const val core = "$group:material:$version"
    }
    object Maps {
        const val version = "3.1.0-beta"
        const val group = "com.google.android.libraries.maps"

        const val core = "$group:maps:$version"
    }
}

object Coroutines {
    const val version = "1.3.7"
    const val group = "org.jetbrains.kotlinx"

    const val core = "$group:kotlinx-coroutines-core:$version"
    const val common = "$group:kotlinx-coroutines-core-common:$version"
    const val android = "$group:kotlinx-coroutines-android:$version"
    const val test = "$group:kotlinx-coroutines-test:$version"
}

object JUnit {
    const val group = "junit"
    const val version = "4.12"

    const val core = "$group:junit:$version"
}

object Groupie {
    const val version = "2.8.0"
    const val group = "com.xwray"

    const val core = "$group:groupie:$version"
    const val extensions = "$group:groupie-kotlin-android-extensions:$version"
}

object Dagger {
    const val group = "com.google.dagger"
    const val version = "2.28.1"

    const val runtime = "$group:dagger:$version"
    const val compiler = "$group:dagger-compiler:$version"
}

object Kimchi {
    const val group = "com.github.inkapplications.kimchi"
    const val version = "1.0.2"

    const val static = "$group:kimchi:$version"
    const val android = "$group:logger-android:$version"
    const val logger = "$group:logger:$version"
    const val analytics = "$group:analytics:$version"

    object Firebase {
        const val group = "com.github.inkapplications.kimchi-firebase-bridge"
        const val version = "1.0.0"

        const val analytics = "$group:analytics:$version"
    }
}

object Karps {
    const val group = "com.github.inkapplications.karps"
    const val version = "0a1a316"
    
    const val parser = "$group:parser:$version"
    const val structures = "$group:structures:$version"
}

object ThreeTenBp {
    const val group = "org.threeten"
    const val version = "1.4.2"

    const val noTzDb = "$group:threetenbp:$version:no-tzdb"

    object Android {
        const val group = "com.jakewharton.threetenabp"
        const val version = "1.2.3"

        const val runtime = "$group:threetenabp:$version"
    }
}

object Room {
    const val group = "androidx.room"
    const val version = "2.2.5"

    const val runtime = "$group:room-runtime:$version"
    const val compiler = "$group:room-compiler:$version"
    const val ktx = "$group:room-ktx:$version"
}


object AndroidX {
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
}

object Google {
    object Material {
        const val version = "1.1.0"
        const val group = "com.google.android.material"

        const val core = "$group:material:$version"
    }
}

object Coroutines {
    const val version = "1.3.7"
    const val group = "org.jetbrains.kotlinx"

    const val core = "$group:kotlinx-coroutines-core:$version"
    const val common = "$group:kotlinx-coroutines-core-common:$version"
    const val android = "$group:kotlinx-coroutines-android:$version"
}

object Groupie {
    const val version = "2.8.0"
    const val group = "com.xwray"

    const val core = "$group:groupie:$version"
    const val extensions = "$group:groupie-kotlin-android-extensions:$version"
}

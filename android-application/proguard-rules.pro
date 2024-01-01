# Kotlin
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.
-keep public class kotlinx.serialization.* { public *; }
-dontwarn kotlinx.serialization.**
-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}

# Okhttp
-keep public class org.bouncycastle.jsse.** { *; }
-dontwarn org.bouncycastle.jsse.**
-keep public class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**
-keep public class org.openjsse.** { *; }
-dontwarn org.openjsse.**

# Mapbox
-keep class com.mapbox.android.telemetry.**
-keep class com.mapbox.android.core.location.**
-keep class android.arch.lifecycle.** { *; }
-keep class com.mapbox.android.core.location.** { *; }
-dontnote com.mapbox.mapboxsdk.**
-dontnote com.mapbox.android.gestures.**
-dontnote com.mapbox.mapboxsdk.plugins.**

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

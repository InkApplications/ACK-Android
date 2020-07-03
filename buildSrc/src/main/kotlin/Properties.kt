import org.gradle.api.Project

fun Project.stringProperty(key: String, default: String): String {
    return if (hasProperty(key)) property(key).toString() else default
}

fun Project.intProperty(key: String, default: Int): Int {
    return if (hasProperty(key)) property(key).toString().toInt() else default
}

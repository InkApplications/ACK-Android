import org.gradle.api.Project

fun Project.stringProperty(key: String, default: String): String {
    return if (hasProperty(key)) property(key).toString() else default
}

fun Project.optionalStringProperty(key: String): String? {
    return if (hasProperty(key)) property(key).toString() else null
}

fun Any?.buildQuote(): String {
    return if (this == null) "null" else "\"$this\""
}

fun Project.intProperty(key: String, default: Int): Int {
    return if (hasProperty(key)) property(key).toString().toInt() else default
}

fun Project.booleanProperty(key: String, default: Boolean): Boolean {
    return if (hasProperty(key)) property(key).toString().toBoolean() else default
}

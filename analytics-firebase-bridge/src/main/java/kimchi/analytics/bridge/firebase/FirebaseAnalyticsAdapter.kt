package kimchi.analytics.bridge.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.SCREEN_VIEW
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kimchi.analytics.AnalyticsWriter
import kimchi.analytics.Property

/**
 * Adapt Kimchi's Analytics reporting to send events to Firebase Analytics.
 */
class FirebaseAnalyticsAdapter(
    private val firebase: FirebaseAnalytics = Firebase.analytics
): AnalyticsWriter {
    private fun ParametersBuilder.applyProperties(properties: List<Property<Any>>) {
        properties.forEach { property ->
            when (property) {
                is Property.IntProperty -> param(property.key, property.value.toLong())
                is Property.DoubleProperty -> param(property.key, property.value)
                is Property.FloatProperty -> param(property.key, property.value.toDouble())
                is Property.LongProperty -> param(property.key, property.value)
                else -> param(property.key, property.value.toString())
            }
        }
    }

    override fun writeEvent(name: String, properties: List<Property<Any>>) {
        firebase.logEvent(name) {
            applyProperties(properties)
        }
    }

    override fun writeProperties(properties: List<Property<Any>>) {
        properties.forEach { property ->
            firebase.setUserProperty(property.key, property.value.toString())
        }
    }

    override fun writeScreen(name: String, properties: List<Property<Any>>) {
        firebase.logEvent(SCREEN_VIEW) {
            param(SCREEN_NAME, name)
            applyProperties(properties)
        }
    }
}

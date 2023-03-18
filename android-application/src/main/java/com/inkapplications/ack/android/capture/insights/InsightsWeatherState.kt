package com.inkapplications.ack.android.capture.insights

/**
 * States for the weather section of the insight screen.
 */
sealed interface InsightsWeatherState {
    /**
     * Indicates that no data has been loaded yet.
     */
    object Initial: InsightsWeatherState

    /**
     * Indicates that no relevant weather packets have been captured yet.
     */
    object Unknown: InsightsWeatherState

    /**
     * Recent Weather Information
     */
    data class DisplayRecent(
        /**
         * Current Temperature, as a formatted string.
         */
        val temperature: String,

        /**
         * The reporting station callsign of the weather data.
         */
        val weatherReporter: String,

        /**
         * The time that the weather data was reported, as a preformatted
         * string in the local time.
         */
        val weatherReportTime: String,

        /**
         * An indication of what iconography to display with the weather.
         */
        val icon: WeatherIcon
    ): InsightsWeatherState

    /**
     * Iconography options that hint at the current weather conditions.
     */
    enum class WeatherIcon {
        Normal,
        Rain,
        Snow,
        Humid,
        Windy,
    }
}

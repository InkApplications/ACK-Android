package com.inkapplications.ack.android.settings

import dagger.Reusable
import javax.inject.Inject

/**
 * Combines all the settings providers in the application into a single provider.
 */
@Reusable
class CompositeSettingsProvider @Inject constructor(
    providers: @JvmSuppressWildcards Set<SettingsProvider>
): SettingsProvider {
    override val settings: List<Setting> = providers.map { it.settings }.flatten()
}

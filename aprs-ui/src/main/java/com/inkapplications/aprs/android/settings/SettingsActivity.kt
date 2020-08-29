package com.inkapplications.aprs.android.settings

import android.view.MenuItem
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.continuePropagation
import com.inkapplications.android.extensions.stopPropagation
import com.inkapplications.aprs.android.BuildConfig
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.prompt.intPrompt
import com.inkapplications.aprs.android.prompt.stringPrompt
import com.inkapplications.kotlin.collectOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kimchi.Kimchi
import kotlinx.android.synthetic.main.settings.*

class SettingsActivity: ExtendedActivity() {
    private lateinit var settingsAccess: SettingsAccess
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.settings)
        setSupportActionBar(settings_toolbar)
        settingsAccess = component.settingsRepository()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        settings_list.adapter = adapter
        settings_list.isNestedScrollingEnabled = false
        settings_version.text = getString(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString())
        settings_version.setOnLongClickListener { stopPropagation { settingsAccess.showAdvancedSettings() } }
    }

    override fun onFirstCreate() {
        super.onFirstCreate()
        settingsAccess.clearState()
    }

    override fun onStart() {
        super.onStart()
        settingsAccess.settingItems.collectOn(foregroundScope) { items -> adapter.update(items) }
        adapter.setOnItemClickListener { item, _ -> onItemClicked(item) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> stopPropagation { onBackPressed() }
        else -> continuePropagation()
    }

    private fun onItemClicked(item: Item<*>) {
        when (item) {
            is SettingStringItem -> onStringClicked(item)
            is SettingIntItem -> onIntClicked(item)
            else -> Kimchi.warn { "Unknown Item type clicked: ${item.javaClass.simpleName}" }
        }
    }

    private fun onIntClicked(item: SettingIntItem) {
        intPrompt(item.setting.name, item.viewModel.value.toInt()) { result ->
            settingsAccess.updateInt(item.setting.key, result)
        }
    }

    private fun onStringClicked(item: SettingStringItem) {
        stringPrompt(item.setting.name, item.viewModel.value) { result ->
            settingsAccess.updateString(item.setting.key, result)
        }
    }
}

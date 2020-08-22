package com.inkapplications.aprs.android.prompt

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inkapplications.aprs.android.R
import kimchi.Kimchi
import kotlinx.android.synthetic.main.prompt_int.view.*
import kotlinx.android.synthetic.main.prompt_string.view.*

inline fun Activity.stringPrompt(
    title: String,
    default: String = "",
    crossinline onResult: (String) -> Unit
) {
    val view = layoutInflater.inflate(R.layout.prompt_string, null)
    view.prompt_string_field.setText(default)

    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(view)
        .setNegativeButton(R.string.prompt_cancel) { _, _ ->
            Kimchi.trace("Prompt Cancelled")
        }
        .setPositiveButton(R.string.prompt_save) { _, _ ->
            val input = view.prompt_string_field.text.toString()
            Kimchi.trace("Prompt Saving <$input>")
            onResult(input)
        }
        .show()
}

inline fun Activity.intPrompt(
    title: String,
    default: Int? = null,
    crossinline onResult: (Int) -> Unit
) {
    val view = layoutInflater.inflate(R.layout.prompt_int, null)
    view.prompt_int_field.setText(default?.toString().orEmpty())

    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(view)
        .setNegativeButton(R.string.prompt_cancel) { _, _ ->
            Kimchi.trace("Prompt Cancelled")
        }
        .setPositiveButton(R.string.prompt_save) { _, _ ->
            val input = view.prompt_int_field.text.toString()
            Kimchi.trace("Prompt Saving <$input>")
            onResult(input.toInt())
        }
        .show()
}
package com.inkapplications.aprs.android.symbol

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import com.inkapplications.aprs.android.R
import com.inkapplications.karps.structures.Symbol
import dagger.Reusable
import kimchi.logger.KimchiLogger
import javax.inject.Inject

/**
 * Create a bitmap icon for an APRS symbol character.
 */
@Reusable
class SymbolFactory @Inject constructor(
    private val context: Context,
    private val locator: SymbolResourceLocator,
    private val logger: KimchiLogger
) {
    private val resources = context.resources

    fun createSymbol(symbol: Symbol): Bitmap {
        val base = locator.getBaseResourceName(symbol).let(::findBitmap)
        val overlay = locator.getOverlayResourceName(symbol)?.let(::findBitmap)

        return if (overlay == null) base else base.withOverlay(overlay)
    }

    private fun findBitmap(name: String): Bitmap {
        return resources.getIdentifier(name, "drawable", context.packageName)
            .let { BitmapFactory.decodeResource(resources, it) }
            ?: BitmapFactory.decodeResource(resources, R.drawable.symbol_94).also {
                logger.error("Unable to find resource for symbol: $name")
            }
    }

    private fun Bitmap.withOverlay(other: Bitmap): Bitmap {
        val bmOverlay = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(this, Matrix(), null)
        canvas.drawBitmap(other, 0f, 0f, null)
        return bmOverlay
    }
}

package com.inkapplications.ack.android.symbol

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.symbolOf
import dagger.Reusable
import javax.inject.Inject

/**
 * Create a bitmap icon for an APRS symbol character.
 */
@Reusable
class AndroidSymbolFactory @Inject constructor(
    private val context: Context,
    private val locator: SymbolResourceLocator
): SymbolFactory {
    private val resources = context.resources

    override val defaultSymbol by lazy {
        createSymbol(symbolOf('"', '/'))
    }

    override fun createSymbol(symbol: Symbol): Bitmap? {
        val base = locator.getBaseResourceName(symbol).let(::findBitmap)
        val overlay = locator.getOverlayResourceName(symbol)?.let(::findBitmap)

        return if (overlay == null) base else base?.withOverlay(overlay)
    }

    private fun findBitmap(name: String): Bitmap? {
        return resources.getIdentifier(name, "drawable", context.packageName)
            .let { BitmapFactory.decodeResource(resources, it) }
    }

    private fun Bitmap.withOverlay(other: Bitmap): Bitmap {
        val bmOverlay = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(this, Matrix(), null)
        canvas.drawBitmap(other, 0f, 0f, null)
        return bmOverlay
    }
}

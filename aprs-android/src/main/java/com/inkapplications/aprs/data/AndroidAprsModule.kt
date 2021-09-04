package com.inkapplications.aprs.data

import android.content.Context
import androidx.room.Room
import com.inkapplications.karps.client.AprsClientModule
import com.inkapplications.karps.parser.ParserModule
import dagger.Module
import dagger.Provides
import kimchi.logger.KimchiLogger
import javax.inject.Singleton

@Module
object AndroidAprsModule {
    @Provides
    @Singleton
    fun aprsAccess(
        logger: KimchiLogger,
        context: Context,
        androidLocationProvider: AndroidLocationProvider,
    ): AprsAccess {
        val audioCapture = AudioDataCapture(logger)
        val audioProcessor = AudioDataProcessor(audioCapture)

        val database = Room.databaseBuilder(context, PacketDatabase::class.java, "aprs_packets")
            .fallbackToDestructiveMigration()
            .build()

        return AndroidAprs(audioProcessor, database.pinsDao(), AprsClientModule.createDataClient(), androidLocationProvider, ParserModule().defaultParser(logger), logger)
    }
}

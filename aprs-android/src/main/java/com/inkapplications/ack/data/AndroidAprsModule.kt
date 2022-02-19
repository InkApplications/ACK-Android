package com.inkapplications.ack.data

import android.content.Context
import androidx.room.Room
import com.inkapplications.ack.data.upgrade.V3Upgrade
import com.inkapplications.ack.client.AprsClientModule
import com.inkapplications.ack.parser.ParserModule
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
        val parser = ParserModule(logger).defaultParser()

        val database = Room.databaseBuilder(context, PacketDatabase::class.java, "aprs_packets")
            .addMigrations(V3Upgrade(parser, logger))
            .build()

        return AndroidAprs(
            audioProcessor = audioProcessor,
            packetDao = database.pinsDao(),
            client = AprsClientModule.createDataClient(),
            locationProvider = androidLocationProvider,
            parser = parser,
            modulator = AndroidAfskModulator(),
            logger = logger,
        )
    }
}

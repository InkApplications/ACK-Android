package com.inkapplications.ack.data

import android.content.Context
import androidx.room.Room
import com.inkapplications.ack.data.upgrade.V3Upgrade
import com.inkapplications.ack.client.AprsClientModule
import com.inkapplications.ack.codec.Ack
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.drivers.AfskDriver
import com.inkapplications.ack.data.drivers.DriverSettingsProvider
import com.inkapplications.ack.data.drivers.InternetDriver
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.data.upgrade.V4Upgrade
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kimchi.logger.KimchiLogger
import kotlinx.datetime.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidAprsModule {
    @Provides
    @Reusable
    fun aprsCodec(
        logger: KimchiLogger,
    ): AprsCodec {
        val logAdapter = object: KimchiLogger by logger {
            override fun debug(message: String, cause: Throwable?) = Unit
            override fun debug(cause: Throwable?, message: () -> String) = Unit
            override fun trace(message: String, cause: Throwable?) = Unit
            override fun trace(cause: Throwable?, message: () -> String) = Unit
        }
        return Ack(logAdapter).defaultParser()
    }

    @Provides
    @Reusable
    fun packetStorage(
        context: Context,
        codec: AprsCodec,
        clock: Clock,
        logger: KimchiLogger,
    ): PacketStorage {
        val database = Room.databaseBuilder(context, PacketDatabase::class.java, "aprs_packets")
            .addMigrations(V3Upgrade(codec, logger))
            .addMigrations(V4Upgrade(codec, logger))
            .build()
        return DaoPacketStorage(database.pinsDao(), codec, clock, logger)
    }

    @Provides
    @Singleton
    fun drivers(
        codec: AprsCodec,
        logger: KimchiLogger,
        androidLocationProvider: AndroidLocationProvider,
        driverSettings: DriverSettingsProvider,
        packetStorage: PacketStorage,
    ): PacketDrivers {
        val audioCapture = AudioDataCapture(logger)
        val audioProcessor = AudioDataProcessor(audioCapture)

        val internet = InternetDriver(
            aprsCodec = codec,
            packetStorage = packetStorage,
            client = AprsClientModule.createDataClient(),
            locationProvider = androidLocationProvider,
            settings = driverSettings,
            logger = logger,
        )
        val afsk = AfskDriver(
            aprsCodec = codec,
            packetStorage = packetStorage,
            audioProcessor = audioProcessor,
            modulator = AndroidAfskModulator(),
            settings = driverSettings,
            logger = logger,
        )

        return PacketDrivers(
            internetDriver = internet,
            afskDriver = afsk,
        )
    }
}

package com.inkapplications.ack.data

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.inkapplications.ack.client.AprsClientModule
import com.inkapplications.ack.codec.Ack
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.adapters.CallsignAdapter
import com.inkapplications.ack.data.adapters.CaptureIdColumnAdapter
import com.inkapplications.ack.data.adapters.InstantAdapter
import com.inkapplications.ack.data.adapters.PacketOriginAdapter
import com.inkapplications.ack.data.drivers.*
import com.inkapplications.android.extensions.bluetooth.BluetoothDeviceAccess
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        val driver = AndroidSqliteDriver(PacketDatabase.Schema, context, "packets.db")
        val database = PacketDatabase(
            driver = driver,
            CapturedPacketEntity.Adapter(
                idAdapter = CaptureIdColumnAdapter,
                timestampAdapter = InstantAdapter,
                packet_originAdapter = PacketOriginAdapter,
                source_callsignAdapter = CallsignAdapter,
                addressee_callsignAdapter = CallsignAdapter,
            ),
        )
        return DaoPacketStorage(database.capturedPacketEntityQueries, codec, clock, logger)
    }

    @Provides
    @Singleton
    fun drivers(
        codec: AprsCodec,
        logger: KimchiLogger,
        androidLocationProvider: AndroidLocationProvider,
        driverSettings: DriverSettingsProvider,
        packetStorage: PacketStorage,
        bluetoothAccess: BluetoothDeviceAccess,
    ): PacketDrivers {
        val audioCapture = AudioDataCapture(logger)
        val audioProcessor = AudioDataProcessor(audioCapture)
        val backgroundScope = CoroutineScope(Dispatchers.Default)

        val internet = InternetDriver(
            aprsCodec = codec,
            packetStorage = packetStorage,
            client = AprsClientModule.createDataClient(),
            locationProvider = androidLocationProvider,
            settings = driverSettings,
            runScope = backgroundScope,
            logger = logger,
        )
        val afsk = AfskDriver(
            aprsCodec = codec,
            packetStorage = packetStorage,
            audioProcessor = audioProcessor,
            modulator = AndroidAfskModulator(),
            settings = driverSettings,
            runScope = backgroundScope,
            logger = logger,
        )

        val tncDriver = TncDriver(
            bluetoothAccess = bluetoothAccess,
            packetStorage = packetStorage,
            ack = codec,
            runScope = backgroundScope,
            logger = logger,
        )

        return PacketDrivers(
            internetDriver = internet,
            afskDriver = afsk,
            tncDriver = tncDriver,
        )
    }
}

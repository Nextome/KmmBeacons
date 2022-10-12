package com.nextome.kbeaconscanner

import android.content.Context
import android.util.Log
import com.nextome.kbeaconscanner.KScanResultParser.asKScanResult
import com.nextome.kbeaconscanner.utils.CFlow
import com.nextome.kbeaconscanner.utils.wrap
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

actual class KBeaconScanner{
    private var regionUUID = DEFAULT_REGION_UUID

    private val scannerFlow = MutableSharedFlow<List<KScanResult>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val rangingRegion = Region(regionUUID, null, null, null)

    private val beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(context).apply {
        beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
    }

    private val _errorObservable = MutableSharedFlow<Exception>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        setScanPeriod(DEFAULT_PERIOD_SCAN)
        setBetweenScanPeriod(DEFAULT_PERIOD_BETWEEEN_SCAN)
    }

    actual fun start() {
        with (beaconManager) {
            stopRangingBeacons(rangingRegion)
            removeAllRangeNotifiers()

            addRangeNotifier { beacons, _ ->
                scannerFlow.tryEmit(
                    beacons
                        .filterNotNull()
                        .map { it.asKScanResult() })
            }

            startRangingBeacons(rangingRegion)
        }
    }

    actual fun observeResults(): CFlow<List<KScanResult>> = scannerFlow.wrap()

    actual fun stop() {
        with (beaconManager) {
            stopRangingBeacons(rangingRegion)
        }
    }

    actual fun setScanPeriod(scanPeriod: Long) {
        with(beaconManager) {
            foregroundScanPeriod = scanPeriod
            backgroundScanPeriod = scanPeriod
            updateScanPeriods()
        }
    }

    actual fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        with (beaconManager) {
            foregroundBetweenScanPeriod = betweenScanPeriod
            backgroundBetweenScanPeriod = betweenScanPeriod
            updateScanPeriods()
        }
    }

    actual fun observeErrors(): CFlow<Exception> {
        return _errorObservable.wrap()
    }

    actual companion object Factory {
        lateinit var context: ApplicationContext

        actual fun init(context: ApplicationContext){
            this.context = context
        }
    }
}
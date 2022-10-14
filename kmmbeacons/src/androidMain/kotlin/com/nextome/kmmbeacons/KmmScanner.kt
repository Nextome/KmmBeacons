package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.KScanResultParser.asKScanResult
import com.nextome.kmmbeacons.data.ApplicationContext
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow
import com.nextome.kmmbeacons.utils.wrap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

internal actual class KmmScanner actual constructor(context: ApplicationContext?) {

    private val applicationContext: ApplicationContext
    private val beaconManager: BeaconManager

    init {
        requireNotNull(context) { "Library must be initialized with KmmBeacons.init(application)" }
        applicationContext = context
        beaconManager = BeaconManager.getInstanceForApplication(applicationContext).apply {
            beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
        }
        setScanPeriod(DEFAULT_PERIOD_SCAN)
        setBetweenScanPeriod(DEFAULT_PERIOD_BETWEEEN_SCAN)
    }

    private var regionUUID = DEFAULT_REGION_UUID

    private val scannerFlow = MutableSharedFlow<List<KScanResult>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val rangingRegion = Region(regionUUID, null, null, null)


    private val _errorObservable = MutableSharedFlow<Exception>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )


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
}
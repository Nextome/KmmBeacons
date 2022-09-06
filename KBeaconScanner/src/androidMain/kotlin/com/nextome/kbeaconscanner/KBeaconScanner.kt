package com.nextome.kbeaconscanner

import android.content.Context
import com.nextome.kbeaconscanner.KScanResultParser.asKScanResult
import com.nextome.kbeaconscanner.utils.CFlow
import com.nextome.kbeaconscanner.utils.wrap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

actual class KBeaconScanner{
    private var regionUUID = DEFAULT_REGION_UUID

    private val scannerFlow = MutableStateFlow(listOf<KScanResult>())

    private val rangingRegion = Region(regionUUID, null, null, null)

    private val beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(context).apply {
        beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
    }

    private val _errorObservable = MutableStateFlow<Exception?>(null)

    init {
        setScanPeriod(DEFAULT_PERIOD_SCAN)
        setBetweenScanPeriod(DEFAULT_PERIOD_BETWEEEN_SCAN)
    }

    actual fun start() {
        with (beaconManager) {
            stopRangingBeacons(rangingRegion)
            removeAllRangeNotifiers()

            addRangeNotifier { beacons, _ ->
                if (beacons.isNotEmpty()) {
                    scannerFlow.tryEmit(
                        beacons
                            .filterNotNull()
                            .map { it.asKScanResult() }
                    )
                }
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
        return _errorObservable.asStateFlow().filterNotNull().wrap()
    }

    actual companion object Factory {
        lateinit var context: ApplicationContext

        actual fun init(context: ApplicationContext){
            this.context = context
        }
    }
}
package com.nextome.kbeaconscanner

import android.util.Log
import com.nextome.kbeaconscanner.KScanResultParser.asKScanResult
import com.nextome.kbeaconscanner.data.ApplicationContext
import com.nextome.kbeaconscanner.data.KScanResult
import com.nextome.kbeaconscanner.utils.CFlow
import com.nextome.kbeaconscanner.utils.wrap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

internal actual class KmmScanner{
    private var regionUUID = DEFAULT_REGION_UUID

    private val rangingRegion = Region(regionUUID, null, null, null)

    private val beaconManager: BeaconManager = BeaconManager.getInstanceForApplication(context).apply {
        beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
    }

    init {
        setScanPeriod(DEFAULT_PERIOD_SCAN)
        setBetweenScanPeriod(DEFAULT_PERIOD_BETWEEEN_SCAN)
    }

    private fun start() {
        Log.e("test", "Start Called")
        with (beaconManager) {
            stopRangingBeacons(rangingRegion)
            removeAllRangeNotifiers()
            startRangingBeacons(rangingRegion)
        }
    }

    actual fun observeResults(): CFlow<List<KScanResult>> = callbackFlow{
        start()

        beaconManager.addRangeNotifier { beacons, _ ->
            trySend(beacons
                .filterNotNull()
                .map { it.asKScanResult() })
        }

        awaitClose { stop() }
    }.wrap()

    private fun stop() {
        Log.e("test", "Stop called")

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

    actual companion object Factory {
        lateinit var context: ApplicationContext

        actual fun init(context: ApplicationContext){
            this.context = context
        }
    }
}
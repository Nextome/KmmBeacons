@file:JvmName("KmmScannerJvm")
package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.KScanResultParser.asKScanResult
import com.nextome.kmmbeacons.data.KScanRegion
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow
import com.nextome.kmmbeacons.utils.wrap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.altbeacon.beacon.*


internal class AndroidKmmScanner: KmmScanner {
    private var isScanning = false
    private var lastScanBeacons = mutableSetOf<Beacon>()

    private var currentScanPeriod = DEFAULT_PERIOD_SCAN
    private var currentBetweenScanPeriod = DEFAULT_PERIOD_BETWEEEN_SCAN

    init {
        setScanPeriod(currentScanPeriod)
        setBetweenScanPeriod(currentBetweenScanPeriod)
    }
    companion object {
        private val beaconManager: BeaconManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BeaconManager.getInstanceForApplication(applicationContext).apply {
                beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
                setEnableScheduledScanJobs(false)
            }
        }
    }


    private val regionsList = mutableListOf<Region>()

    override fun start() {
        isScanning = true
        provideDefaultRegionIfNecessary()
        with (beaconManager) {
            removeAllRangeNotifiers()

            regionsList.forEach { region ->
                stopRangingBeacons(region)

                addRangeNotifier { beacons, _ ->
                    lastScanBeacons.addAll(beacons)
                }

                startRangingBeacons(region)
            }
        }
    }

    private fun provideDefaultRegionIfNecessary() {
        if (regionsList.isEmpty()) {
            regionsList.addAll(
                listOf(Region("all-beacons-region", null, null, null))
            )
        }
    }

    override fun observeResults(): CFlow<List<KScanResult>> = flow{
        while(true) {
            val beaconsToEmit = lastScanBeacons.toMutableList()
            lastScanBeacons.clear()
            emit(beaconsToEmit.map { it.asKScanResult() })
            delay(currentScanPeriod + currentBetweenScanPeriod)
        }
    }.wrap()

    override fun stop() {
        isScanning = false

        with (beaconManager) {
            regionsList.forEach { region ->
                stopRangingBeacons(region)
            }
        }
    }

    override fun setScanPeriod(scanPeriod: Long) {
        currentScanPeriod = scanPeriod
        with(beaconManager) {
            foregroundScanPeriod = currentScanPeriod
            backgroundScanPeriod = currentScanPeriod
            updateScanPeriods()
        }
    }

    override fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        currentBetweenScanPeriod = betweenScanPeriod

        with (beaconManager) {
            foregroundBetweenScanPeriod = currentBetweenScanPeriod
            backgroundBetweenScanPeriod = currentBetweenScanPeriod
            updateScanPeriods()
        }
    }

    override fun observeErrors(): CFlow<Exception> {
        return flowOf<Exception>().wrap()
    }

    override fun setAndroidRegions(region: List<KScanRegion>) {
        if (isScanning) {
            throw Exception("You can not add new regions while scanning.")
        }

        with(regionsList) {
            clear()
            addAll(region.map { it.asAndroidRegion() })
        }
    }

    override fun setIosRegions(regions: List<KScanRegion>) {
        return
    }

    private fun KScanRegion.asAndroidRegion() = Region(
        uuid, Identifier.parse(uuid), null, null
    )
}

internal actual fun getKmmScanner(): KmmScanner = AndroidKmmScanner()
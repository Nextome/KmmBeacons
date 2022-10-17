package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.KScanResultParser.asKScanResult
import com.nextome.kmmbeacons.data.ApplicationContext
import com.nextome.kmmbeacons.data.KScanRegion
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow
import com.nextome.kmmbeacons.utils.wrap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import org.altbeacon.beacon.*

internal actual class KmmScanner actual constructor(
    context: ApplicationContext?,
) {

    private val applicationContext: ApplicationContext
    private val beaconManager: BeaconManager

    private var isScanning = false
    private var lastScanBeacons = mutableSetOf<Beacon>()

    private var currentScanPeriod = DEFAULT_PERIOD_SCAN
    private var currentBetweenScanPeriod = DEFAULT_PERIOD_BETWEEEN_SCAN

    init {
        requireNotNull(context) { "Library must be initialized with KmmBeacons.init(application)" }
        applicationContext = context
        beaconManager = BeaconManager.getInstanceForApplication(applicationContext).apply {
            beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
        }
        setScanPeriod(currentScanPeriod)
        setBetweenScanPeriod(currentBetweenScanPeriod)
    }

    private val regionsList = mutableListOf<Region>()

    private val scannerFlow = MutableSharedFlow<List<KScanResult>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )


    private val _errorObservable = MutableSharedFlow<Exception>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )


    actual fun start() {
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

    actual fun observeResults(): CFlow<List<KScanResult>> = flow{
        while(true) {
            val beaconsToEmit = lastScanBeacons.toMutableList()
            lastScanBeacons.clear()
            emit(beaconsToEmit.map { it.asKScanResult() })
            delay(currentScanPeriod + currentBetweenScanPeriod)
        }
    }.wrap()

    actual fun stop() {
        isScanning = false

        with (beaconManager) {
            regionsList.forEach { region ->
                stopRangingBeacons(region)
            }
        }
    }

    actual fun setScanPeriod(scanPeriod: Long) {
        currentScanPeriod = scanPeriod
        with(beaconManager) {
            foregroundScanPeriod = currentScanPeriod
            backgroundScanPeriod = currentScanPeriod
            updateScanPeriods()
        }
    }

    actual fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        currentBetweenScanPeriod = betweenScanPeriod

        with (beaconManager) {
            foregroundBetweenScanPeriod = currentBetweenScanPeriod
            backgroundBetweenScanPeriod = currentBetweenScanPeriod
            updateScanPeriods()
        }
    }

    actual fun observeErrors(): CFlow<Exception> {
        return _errorObservable.wrap()
    }
    actual fun setAndroidRegions(region: List<KScanRegion>) {
        if (isScanning) {
            _errorObservable.tryEmit(Exception("You can not add new regions while scanning."))
            return
        }

        with(regionsList) {
            clear()
            addAll(region.map { it.asAndroidRegion() })
        }
    }

    actual fun setIosRegions(regions: List<KScanRegion>) {
        return
    }

    private fun KScanRegion.asAndroidRegion() = Region(
        uuid, Identifier.parse(uuid), null, null
    )
}
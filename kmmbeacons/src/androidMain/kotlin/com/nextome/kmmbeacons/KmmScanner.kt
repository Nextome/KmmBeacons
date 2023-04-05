@file:JvmName("KmmScannerJvm")
package com.nextome.kmmbeacons

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.nextome.kmmbeacons.KScanResultParser.asKScanResult
import com.nextome.kmmbeacons.data.KScanRecord
import com.nextome.kmmbeacons.data.KScanRegion
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow
import com.nextome.kmmbeacons.utils.wrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.altbeacon.beacon.*
import java.util.concurrent.ConcurrentHashMap


internal class AndroidKmmScanner: KmmScanner {
    private var isScanning = false
    private var lastScanBeacons = ConcurrentHashMap<String, KScanResult>()
    private var lastScanNonBeacons = ConcurrentHashMap<String, KScanRecord>()

    private var currentScanPeriod = DEFAULT_PERIOD_SCAN
    private var currentBetweenScanPeriod = DEFAULT_PERIOD_BETWEEEN_SCAN

    private val scanBeaconFlow = MutableSharedFlow<List<KScanResult>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    private val scanNonBeaconFlow = MutableSharedFlow<List<KScanRecord>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private var beaconEmitJob: Job? = null
    private var nonBeaconEmitJob: Job? = null

    init {
        setScanPeriod(currentScanPeriod)
        setBetweenScanPeriod(currentBetweenScanPeriod)
    }
    companion object {
        private val beaconManager: BeaconManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BeaconManager.getInstanceForApplication(applicationContext).apply {
                beaconParsers.add(BeaconParser().setBeaconLayout(BEACON_LAYOUT_IBEACON))
                setEnableScheduledScanJobs(false)
                isRegionStatePersistenceEnabled = false
            }
        }
    }


    private val regionsList = mutableListOf<Region>()

    @SuppressLint("MissingPermission")
    override fun start() {
        isScanning = true

        startEmittingBeaconsJob()
        startEmittingNonBeaconsJob()

        provideDefaultRegionIfNecessary()
        with (beaconManager) {
            removeAllRangeNotifiers()

            regionsList.forEach { region ->
                stopRangingBeacons(region)

                setNonBeaconLeScanCallback { device, rssi, scanRecord ->
                    var name: String? = null

                    try {
                        name = device.name
                    } catch (e: Exception) {
                        // user doesn't have BLUETOOTH_CONNECT permissions
                    }

                    lastScanNonBeacons[device.address] = KScanRecord(
                        deviceAddress = device.address,
                        deviceName = name,
                        rawBytes = scanRecord)
                }

                addRangeNotifier { beacons, _ ->
                    beacons.forEach {
                        with (it.asKScanResult()) {
                            lastScanBeacons[getKey()] = this
                        }
                    }
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
    override fun observeResults(): CFlow<List<KScanResult>> = scanBeaconFlow.wrap()
    override fun observeNonBeacons(): CFlow<List<KScanRecord>> = scanNonBeaconFlow.wrap()

    private fun startEmittingBeaconsJob() {
        if (beaconEmitJob == null) {
            beaconEmitJob = scope.launch {
                while (true) {
                    try {
                        val beaconsToEmit = lastScanBeacons.values.toList()
                        lastScanBeacons.clear()
                        scanBeaconFlow.tryEmit(beaconsToEmit)
                    } catch (e: NoSuchElementException) {
                        // issues during iteration on values
                    }

                    delay(currentScanPeriod + currentBetweenScanPeriod)
                }
            }
        }
    }

    private fun startEmittingNonBeaconsJob() {
        if (nonBeaconEmitJob == null) {
            nonBeaconEmitJob = scope.launch {
                while (true) {
                    try {
                        val nonBeaconsToEmit = lastScanNonBeacons.values.toList()
                        lastScanNonBeacons.clear()
                        scanNonBeaconFlow.tryEmit(nonBeaconsToEmit)
                        delay(currentScanPeriod + currentBetweenScanPeriod)
                    } catch (e: NoSuchElementException) {
                        // issues during iteration on values
                    }
                }
            }
        }
    }

    override fun stop() {
        isScanning = false

        beaconEmitJob?.cancel()
        beaconEmitJob = null

        nonBeaconEmitJob?.cancel()
        nonBeaconEmitJob = null

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
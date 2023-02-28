package com.nextome.kmmbeacons

import co.touchlab.kermit.Logger
import co.touchlab.stately.collections.ConcurrentMutableList
import com.nextome.kmmbeacons.data.asKScanResult
import com.nextome.kmmbeacons.utils.CFlow
import com.nextome.kmmbeacons.utils.wrap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLBeaconRegion
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

internal class IosScannerManager: NSObject(), CLLocationManagerDelegateProtocol {
    private val locationManager: CLLocationManager = CLLocationManager()
    private val scanRegionList = mutableListOf<CLBeaconRegion>()

    private val _errorObservable = MutableSharedFlow<Exception>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val TAG = "KBeaconScanner"
    private val NOT_DETERMINED = 0
    private val RESTRICTED = 1
    private val DENIED = 2
    private val AUTHORIZED_ALWAYS = 3
    private val AUTHORIZED_WHEN_IN_USE = 4

    private var scanTime  = DEFAULT_PERIOD_SCAN
    private var betweenScanTime = DEFAULT_PERIOD_BETWEEEN_SCAN

    private var lastScanBeacons = ConcurrentMutableList<CLBeacon>()
    private var isScanning = false

    init {
        locationManager.delegate = this
        locationManager.allowsBackgroundLocationUpdates = true
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        Logger.d(tag = TAG){"locationManagerDidChangeAuthorization"}
        startRegionOrAskPermissions()
    }

    private fun startRegionOrAskPermissions(){
        when (locationManager.authorizationStatus){
            NOT_DETERMINED -> locationManager.requestWhenInUseAuthorization()
            AUTHORIZED_WHEN_IN_USE, AUTHORIZED_ALWAYS -> startRangingForRegions()
            DENIED, RESTRICTED -> _errorObservable.tryEmit(Exception("Localization permission denied"))
            else -> locationManager.requestLocation()
        }
    }

    private fun startRangingForRegions(){
        Logger.d(tag = TAG) { "startRangingForRegions"}
        isScanning = true
        scanRegionList.forEach {
            locationManager.startRangingBeaconsInRegion(it)
        }
    }

    override fun locationManager(
        manager: CLLocationManager,
        didRangeBeacons: List<*>,
        inRegion: CLBeaconRegion
    ) {
        val rangedBeacons = didRangeBeacons.map { it as CLBeacon }
        lastScanBeacons.addAll(rangedBeacons)
    }

    override fun locationManager(
        manager: CLLocationManager,
        rangingBeaconsDidFailForRegion: CLBeaconRegion,
        withError: NSError
    ) {
        Logger.e(tag = TAG) { "BleScanner Exception -> ${withError.localizedDescription}" }
        _errorObservable.tryEmit(Exception("Scan failed with: ${withError.localizedDescription}"))
    }

    internal fun setScanPeriod(scanPeriod: Long) {
        scanTime = scanPeriod
    }

    internal fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        betweenScanTime = betweenScanPeriod
    }

    internal fun start() {
        startRegionOrAskPermissions()
    }

    internal fun observeResults() = flow{
        while (true) {
            val beaconsToEmit = lastScanBeacons.toMutableList()
            lastScanBeacons.clear()
            emit(beaconsToEmit.asKScanResult())
            delay(scanTime + betweenScanTime)
        }
    }.wrap()

    internal fun stop() {
        isScanning = false

        locationManager.rangedRegions.forEach {
            (it as? CLBeaconRegion)?.let { region ->
                locationManager.stopRangingBeaconsInRegion(region)
            }
        }
    }

    internal fun observeErrors(): CFlow<Exception> {
        return _errorObservable.wrap()
    }

    internal fun setIosRegions(regions: List<CLBeaconRegion>) {
        if (isScanning) {
            _errorObservable.tryEmit(Exception("You can not add new regions while scanning."))
            return
        }


        with (scanRegionList) {
            clear()
            addAll(regions)
        }
    }
}
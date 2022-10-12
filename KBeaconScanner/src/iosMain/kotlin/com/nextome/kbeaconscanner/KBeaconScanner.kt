package com.nextome.kbeaconscanner

import co.touchlab.kermit.Logger
import com.nextome.kbeaconscanner.utils.CFlow
import com.nextome.kbeaconscanner.utils.wrap
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.Foundation.NSUUID
import platform.darwin.NSObject

actual class KBeaconScanner actual constructor(): NSObject(), CLLocationManagerDelegateProtocol{
    private val locationManager: CLLocationManager = CLLocationManager()
    private val regionList = listOf(
        CLBeaconRegion(
            uUID = NSUUID("777E6B3A-4E6A-40B4-9E02-975E61DF3C27"),
            identifier = "777E6B3A-4E6A-40B4-9E02-975E61DF3C27"),
        CLBeaconRegion(
            uUID = NSUUID("E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"),
            identifier = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0"),
        CLBeaconRegion(
            uUID = NSUUID("3C8836E0-8FE6-11E8-9EB6-529269FB1459"),
            identifier = "3C8836E0-8FE6-11E8-9EB6-529269FB1459"),
        CLBeaconRegion(
            uUID = NSUUID("ACFD065E-C3C0-11E3-9BBE-1A514932AC01"),
            identifier = "ACFD065E-C3C0-11E3-9BBE-1A514932AC01"),
        CLBeaconRegion(
            uUID = NSUUID("23A01AF0-232A-4518-9C0E-323FB773F5EF"),
            identifier = "23A01AF0-232A-4518-9C0E-323FB773F5EF"),
        CLBeaconRegion(
            uUID = NSUUID("4F0358E0-2EE7-11E4-8C21-0800200C9A66"),
            identifier = "4F0358E0-2EE7-11E4-8C21-0800200C9A66"),
        CLBeaconRegion(
            uUID = NSUUID("F7826DA6-4FA2-4E98-8024-BC5B71E0893E"),
            identifier = "F7826DA6-4FA2-4E98-8024-BC5B71E0893E"),
    )

    private val result = MutableSharedFlow<List<KScanResult>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    private val error = MutableSharedFlow<Exception>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val TAG = "KBeaconScanner"
    private val NOT_DETERMINED = 0
    private val RESTRICTED = 1
    private val DENIED = 2
    private val AUTHORIZED_ALWAYS = 3
    private val AUTHORIZED_WHEN_IN_USE = 4


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
            DENIED, RESTRICTED -> error.tryEmit(Exception("Localization permission denied"))
            else -> locationManager.requestLocation()
        }
    }
    private fun startRangingForRegions(){
        Logger.d(tag = TAG) { "startRangingForRegions"}
        regionList.forEach {
            locationManager.startRangingBeaconsInRegion(it)
        }
    }

    override fun locationManager(
        manager: CLLocationManager,
        didRangeBeacons: List<*>,
        inRegion: CLBeaconRegion
    ) {
        val rangedBeacons = didRangeBeacons.map { it as CLBeacon }
        val resultBeacons = rangedBeacons.filter { it.accuracy >= 0 }.map {
            KScanResult(
                it.UUID.UUIDString,
                it.rssi.toDouble(),
                it.minor.intValue,
                it.major.intValue,
                0,
                it.accuracy,
                it.proximity.asKScanProximity()
            )
        }

        result.tryEmit(resultBeacons)
    }

    override fun locationManager(
        manager: CLLocationManager,
        rangingBeaconsDidFailForRegion: CLBeaconRegion,
        withError: NSError
    ) {
        Logger.e(tag = TAG) { "BleScanner Exception -> ${withError.localizedDescription}" }
        error.tryEmit(Exception("Scan failed with: ${withError.localizedDescription}"))
    }

    actual fun setScanPeriod(scanPeriod: Long) {
       //NOT IMPLEMENTED
    }

    actual fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        //NOT IMPLEMENTED
    }

    actual fun start() {
        startRegionOrAskPermissions()
    }

    actual fun observeResults(): CFlow<List<KScanResult>> {
        return result.wrap()
    }

    actual fun stop() {
        locationManager.rangedRegions.forEach {
            (it as? CLBeaconRegion)?.let { region ->
                locationManager.stopRangingBeaconsInRegion(region)
            }
        }
    }

    actual fun observeErrors(): CFlow<Exception> {
        return error.wrap()
    }

    actual companion object Factory {
        actual fun init(context: ApplicationContext) = Unit
    }

}


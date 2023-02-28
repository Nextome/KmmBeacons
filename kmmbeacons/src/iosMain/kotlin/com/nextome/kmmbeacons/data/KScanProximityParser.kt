package com.nextome.kmmbeacons.data

import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLProximity

internal fun CLProximity.asKScanProximity(): KScanProximity {
   return when(this){
        CLProximity.CLProximityFar -> KScanProximity.FAR
        CLProximity.CLProximityImmediate -> KScanProximity.IMMEDIATE
        CLProximity.CLProximityNear -> KScanProximity.NEAR
        CLProximity.CLProximityUnknown -> KScanProximity.UNKNOWN
        else -> KScanProximity.UNKNOWN
    }
}

internal fun List<CLBeacon>.asKScanResult() =
    filter { it.accuracy >= 0 }.map {
        KScanResult(
            uuid = it.UUID.UUIDString,
            major = it.major.intValue,
            minor = it.minor.intValue,
            rssi = it.rssi.toDouble(),
            txPower = 0,
            accuracy = it.accuracy,
            proximity = it.proximity.asKScanProximity()
        )
    }
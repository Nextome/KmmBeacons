package com.nextome.kmmbeacons.data

import platform.CoreLocation.CLBeacon
import platform.CoreLocation.CLProximity

fun CLProximity.asKScanProximity(): KScanProximity {
   return when(this){
        CLProximity.CLProximityFar -> KScanProximity.FAR
        CLProximity.CLProximityImmediate -> KScanProximity.IMMEDIATE
        CLProximity.CLProximityNear -> KScanProximity.NEAR
        CLProximity.CLProximityUnknown -> KScanProximity.UNKNOWN
        else -> KScanProximity.UNKNOWN
    }
}

fun List<CLBeacon>.asKScanResult() =
    filter { it.accuracy >= 0 }.map {
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
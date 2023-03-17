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

internal fun CLBeacon.asKScanResult(): KScanResult =
    KScanResult(
        uuid = this.UUID.UUIDString,
        major = this.major.intValue,
        minor = this.minor.intValue,
        rssi = this.rssi.toDouble(),
        txPower = 0,
        accuracy = this.accuracy,
        proximity = this.proximity.asKScanProximity(),
        bluetoothName = null,
    )
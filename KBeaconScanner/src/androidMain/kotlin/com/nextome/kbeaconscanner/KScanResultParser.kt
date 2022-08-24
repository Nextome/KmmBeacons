package com.nextome.kbeaconscanner

import org.altbeacon.beacon.Beacon

object KScanResultParser {
    fun Beacon.asKScanResult() = KScanResult(
        uuid = this.id1.toString(),
        major = this.id2.toInt(),
        minor = this.id3.toInt(),
        rssi = this.rssi.toDouble(),
        txPower = this.txPower,
        accuracy = 0.0,
        proximity = KScanProximity.UNKNOWN
    )
}
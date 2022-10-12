package com.nextome.kbeaconscanner.data


data class KScanResult(
    val uuid: String,
    val rssi: Double,
    val minor: Int,
    val major: Int,
    val txPower: Int,
    val accuracy: Double?,
    val proximity: KScanProximity,
    var rawBytes: Array<Byte> = arrayOf()
)




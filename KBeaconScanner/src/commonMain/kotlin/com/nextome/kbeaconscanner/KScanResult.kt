package com.nextome.kbeaconscanner

data class KScanResult(
    val uuid: String,
    val rssi: Double,
    val minor: Int,
    val major: Int,
    val txPower: Int = 0,
)

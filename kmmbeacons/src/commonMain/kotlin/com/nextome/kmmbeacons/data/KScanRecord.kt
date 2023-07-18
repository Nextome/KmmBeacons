package com.nextome.kmmbeacons.data

data class KScanRecord(
    val deviceName: String?,
    val deviceAddress: String,
    val rawBytes: ByteArray,
    val rssi: Int,
)
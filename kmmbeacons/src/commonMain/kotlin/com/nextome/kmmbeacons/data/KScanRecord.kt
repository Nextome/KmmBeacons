package com.nextome.kmmbeacons.data

data class KScanRecord(
    val deviceAddress: String,
    val rawBytes: ByteArray,
)
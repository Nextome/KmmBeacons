package com.nextome.kbeaconscanner

import com.nextome.kbeaconscanner.utils.CFlow

const val DEFAULT_PERIOD_SCAN = 1000L
const val DEFAULT_PERIOD_BETWEEEN_SCAN = 250L
const val DEFAULT_REGION_UUID = "KBeaconScannerRanging"
const val BEACON_LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25,i:0-56"

expect class KBeaconScanner() {
    fun setScanPeriod(scanPeriod: Long)
    fun setBetweenScanPeriod(betweenScanPeriod: Long)
    fun start()
    fun observeResults(): CFlow<List<KScanResult>>
    fun observeErrors(): CFlow<Exception>
    fun stop()

    companion object Factory {
        fun init(context: ApplicationContext)
    }
}
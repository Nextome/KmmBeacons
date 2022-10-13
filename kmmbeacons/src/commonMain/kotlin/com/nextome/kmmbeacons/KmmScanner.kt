package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.ApplicationContext
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow

const val DEFAULT_PERIOD_SCAN = 1000L
const val DEFAULT_PERIOD_BETWEEEN_SCAN = 250L
const val DEFAULT_REGION_UUID = "KBeaconScannerRanging"
const val BEACON_LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25,i:0-56"

internal expect class KmmScanner() {
    fun setScanPeriod(scanPeriod: Long)
    fun setBetweenScanPeriod(betweenScanPeriod: Long)
    fun observeResults(): CFlow<List<KScanResult>>

    companion object Factory {
        fun init(context: ApplicationContext)
    }
}
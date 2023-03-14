package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.KScanRecord
import com.nextome.kmmbeacons.data.KScanRegion
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow

const val DEFAULT_PERIOD_SCAN = 1000L
const val DEFAULT_PERIOD_BETWEEEN_SCAN = 250L
const val BEACON_LAYOUT_IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25,i:0-56"

internal interface KmmScanner {
    fun setScanPeriod(scanPeriod: Long)
    fun setBetweenScanPeriod(betweenScanPeriod: Long)
    fun observeResults(): CFlow<List<KScanResult>>

    fun observeNonBeacons(): CFlow<List<KScanRecord>>

    fun setIosRegions(regions: List<KScanRegion>)
    fun setAndroidRegions(region: List<KScanRegion>)

    fun observeErrors(): CFlow<Exception>
    fun start()
    fun stop()
}

internal expect fun getKmmScanner(): KmmScanner
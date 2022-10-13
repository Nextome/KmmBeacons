package com.nextome.kbeaconscanner

import com.nextome.kbeaconscanner.data.ApplicationContext
import com.nextome.kbeaconscanner.data.KScanResult
import com.nextome.kbeaconscanner.utils.CFlow

abstract class KmmBeaconsBase(
    context: ApplicationContext?,
){
    init {
        context?.let { KmmScanner.init(it) }
    }

    private val scanner = KmmScanner()

    fun setScanPeriod(scanPeriod: Long) = scanner.setScanPeriod(scanPeriod)
    fun setBetweenScanPeriod(betweenScanPeriod: Long) = scanner.setBetweenScanPeriod(betweenScanPeriod)
    fun observeResults(): CFlow<List<KScanResult>> = scanner.observeResults()
}
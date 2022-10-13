package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.ApplicationContext
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow

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
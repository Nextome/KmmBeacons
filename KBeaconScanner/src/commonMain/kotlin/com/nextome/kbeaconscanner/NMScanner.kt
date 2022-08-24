package com.nextome.kbeaconscanner

import com.nextome.kbeaconscanner.utils.CFlow


class NMScanner {
    val scanner = KBeaconScanner()

    fun setScanPeriod(scanPeriod: Long){
        scanner.setScanPeriod(scanPeriod)
    }
    fun setBetweenScanPeriod(betweenScanPeriod: Long){
        scanner.setBetweenScanPeriod(betweenScanPeriod)
    }
    fun start(){
        scanner.start()
    }
    fun observeResults(): CFlow<List<KScanResult>> {
        return scanner.observeResults()
    }
    fun stop(){
        scanner.stop()
    }

    fun observeErrors(): CFlow<Exception>{
        return scanner.observeErrors()
    }
}
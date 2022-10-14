package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.ApplicationContext
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow

class KmmBeacons{
    private val scanner = KmmScanner(context)

    fun setScanPeriod(scanPeriod: Long) = scanner.setScanPeriod(scanPeriod)
    fun setBetweenScanPeriod(betweenScanPeriod: Long) = scanner.setBetweenScanPeriod(betweenScanPeriod)
    fun observeResults(): CFlow<List<KScanResult>> = scanner.observeResults()
    fun observeErrors(): CFlow<Exception> = scanner.observeErrors()
    fun startScan() = scanner.start()
    fun stopScan() = scanner.stop()

    companion object Factory {
        var context: ApplicationContext? = null

        fun init(context: ApplicationContext){
            this.context = context
        }
    }
}
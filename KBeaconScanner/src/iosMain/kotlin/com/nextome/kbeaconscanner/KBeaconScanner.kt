package com.nextome.kbeaconscanner

import com.nextome.kbeaconscanner.utils.CFlow

actual class KBeaconScanner {
    actual fun setScanPeriod(scanPeriod: Long) {
    }

    actual fun setBetweenScanPeriod(betweenScanPeriod: Long) {
    }

    actual fun start() {
    }

    actual fun observeResults(): CFlow<List<KScanResult>> {
        TODO("Not yet implemented")
    }

    actual fun observeErrors(): CFlow<Exception> {
        TODO("Not yet implemented")
    }

    actual fun stop() {
    }

    actual companion object Factory {
        actual fun init(context: ApplicationContext){

        }
    }


}
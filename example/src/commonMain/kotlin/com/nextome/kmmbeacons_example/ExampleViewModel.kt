package com.nextome.kmmbeacons_example

import com.nextome.kmmbeacons.KmmBeacons
import com.nextome.kmmbeacons.data.KScanRegion

class ExampleViewModel {
    val scanner = KmmBeacons()

    init {

    }

    suspend fun startScanAndObserveResults() {
        scanner.startScan()
        scanner.stopScan()
        scanner.observeResults().collect { results ->
            println("Found ${results.size} beacons:")
            results.forEach {
                println("""
                    UUID: ${it.uuid}
                    Major: ${it.major}
                    Minor: ${it.minor}
                    RSSI: ${it.rssi}
                """.trimIndent())
            }
        }

        scanner.setIosRegions(
            listOf(KScanRegion(uuid = "B39A8A15-1A82-4D24-A311-EBF122BA6AF9"))
        )

        scanner.setScanPeriod(1000L)
        scanner.setBetweenScanPeriod(250L)
    }
}
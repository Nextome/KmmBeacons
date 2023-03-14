package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.KScanRecord
import com.nextome.kmmbeacons.data.KScanRegion
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow
import com.nextome.kmmbeacons.utils.wrap
import kotlinx.coroutines.flow.flowOf

import platform.CoreLocation.*
import platform.Foundation.NSUUID

// IosKmmScanner can't implement both KmmScanner and NSObject for
// https://github.com/JetBrains/kotlin-native/issues/2725
internal class IosKmmScanner: KmmScanner {
    private val iosScannerManager = IosScannerManager()

    override fun setScanPeriod(scanPeriod: Long) {
        iosScannerManager.setScanPeriod(scanPeriod)
    }

    override fun setBetweenScanPeriod(betweenScanPeriod: Long) {
        iosScannerManager.setBetweenScanPeriod(betweenScanPeriod)
    }

    override fun observeResults(): CFlow<List<KScanResult>> {
        return iosScannerManager.observeResults()
    }

    override fun observeNonBeacons(): CFlow<List<KScanRecord>> {
        return flowOf<List<KScanRecord>>().wrap()
    }

    override fun setIosRegions(regions: List<KScanRegion>) {
        iosScannerManager.setIosRegions(regions.map { it.asCLBeaconRegion() })
    }

    override fun setAndroidRegions(region: List<KScanRegion>) {
        // not supported on iOS
        return
    }

    override fun observeErrors(): CFlow<Exception> {
        return iosScannerManager.observeErrors()
    }

    override fun start() {
        iosScannerManager.start()
    }

    override fun stop() {
        iosScannerManager.stop()
    }

}

internal actual fun getKmmScanner(): KmmScanner = IosKmmScanner()

private fun KScanRegion.asCLBeaconRegion() = CLBeaconRegion(
    uUID = NSUUID(uuid),
    identifier = uuid,
)
package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.KScanRecord
import com.nextome.kmmbeacons.data.KScanRegion
import com.nextome.kmmbeacons.data.KScanResult
import com.nextome.kmmbeacons.utils.CFlow

class KmmBeacons {
    private val scanner = getKmmScanner()

    /**
     * Starts beacon scanning.
     */
    fun startScan() = scanner.start()

    /**
     * Stops beacon scanning.
     */
    fun stopScan() = scanner.stop()

    /**
     * Sets the time of each bluetooth scan with the given [scanPeriod] in millis.
     */
    fun setScanPeriod(scanPeriod: Long) = scanner.setScanPeriod(scanPeriod)

    /**
     * Sets the time between each bluetooth scan with the given [betweenScanPeriod] in millis.
     */
    fun setBetweenScanPeriod(betweenScanPeriod: Long) = scanner.setBetweenScanPeriod(betweenScanPeriod)

    /**
     * Observe the scan results.
     * Note that results will be delivered each scanPeriod + betweenScanPeriod time.
     * It is possible to adjust delivery time with [setScanPeriod] and [setBetweenScanPeriod].
     */
    fun observeResults(): CFlow<List<KScanResult>> = scanner.observeResults()

    /**
     * Observe non-Beacon BLE Devices raw data.
     * On iOS, this always returns an empty list due to OS restrictions.
     * Results with be delivered according times set with [setScanPeriod] and [setBetweenScanPeriod].
     */
    fun observeNonBeacons(): CFlow<List<KScanRecord>> = scanner.observeNonBeacons()

    /**
     * Listen for errors during scans;
     */
    fun observeErrors(): CFlow<Exception> = scanner.observeErrors()

    /**
     * Sets given [regions] for iOS devices.
     * iOS scanning needs regions to be defined to recognize beacons with the given uuid.
     * If no region is provided, iOS scan will not recognize any beacons.
     */
    fun setIosRegions(regions: List<KScanRegion>) = scanner.setIosRegions(regions)

    /**
     * Sets given [regions] for Android devices.
     * If no region is provided, Android will return beacons with all UUIDS.
     */
    fun setAndroidRegions(regions: List<KScanRegion>) = scanner.setAndroidRegions(regions)
}
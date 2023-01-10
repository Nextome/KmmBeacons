package com.nextome.kmmbeacons

import com.nextome.kmmbeacons.data.KScanProximity
import com.nextome.kmmbeacons.data.KScanResult
import org.altbeacon.beacon.Beacon

object KScanResultParser {
    fun Beacon.asKScanResult(): KScanResult {
        val rssiToDouble = rssi.toDouble()
        val accuracy = calculateAccuracy(txPower, rssiToDouble)
        val proximity = calculateProximity(accuracy)

        return KScanResult(
            uuid = this.id1.toString(),
            major = this.id2.toInt(),
            minor = this.id3.toInt(),
            rssi = rssiToDouble,
            txPower = this.txPower,
            accuracy = calculateAccuracy(txPower, rssiToDouble),
            proximity = proximity)
    }

    private fun calculateAccuracy(txPower: Int, rssi: Double): Double {
        if (rssi == 0.0) {
            return -1.0 // if we cannot determine accuracy, return -1.
        }

        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            Math.pow(ratio, 10.0)
        } else {
            val accuracy = 0.89976 * Math.pow(ratio, 7.7095) + 0.111
            accuracy
        }
    }

    private fun calculateProximity(accuracy: Double): KScanProximity {
        if (accuracy < 0) {
            return KScanProximity.UNKNOWN
        }
        if (accuracy < 0.5) {
            return KScanProximity.IMMEDIATE
        }

        // forums say 3.0 is the near/far threshold,
        // but it looks to be based on experience that this is 4.0
        if (accuracy > 4.0) {
            // if it is > 4.0 meters, call it far
            return KScanProximity.FAR
        }

        return KScanProximity.NEAR
    }
}
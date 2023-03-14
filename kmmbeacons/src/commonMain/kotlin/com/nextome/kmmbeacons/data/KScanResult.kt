package com.nextome.kmmbeacons.data


data class KScanResult(
    /**
     * Beacon UUID (id1)
     */
    val uuid: String,
    /**
     * Beacon major (id2)
     */
    val major: Int,
    /**
     * Beacon minor (id3)
     */
    val minor: Int,
    /**
     * The measured signal strength of the Bluetooth packet.
     */
    val rssi: Double,
    /**
     * The calibrated measured Tx power of the Beacon in RSSI
     * This value is baked into an Beacon when it is manufactured, and
     * it is transmitted with each packet.
     *
     * This is only available on Android.
     */
    val txPower: Int,

    /**
     * Distance estimate to the beacon in meters.
     * On iOS, this equals to CLLocationAccuracy.
     */
    val accuracy: Double?,
    /**
     * An estimate of how far the Beacon is away.
     */
    val proximity: KScanProximity,
    /**
     * Raw bytes of beacon advertisement.
     * This is available on Android only.
     */
    var rawBytes: Array<Byte> = arrayOf()
) {
    /**
     * Returns an unique key that identifies this beacon
     */
    fun getKey() = "${uuid};${major};${minor}"
}




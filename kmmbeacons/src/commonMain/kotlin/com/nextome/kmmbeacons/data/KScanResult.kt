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
     * Bluetooth name of the devices.
     * if detected by the OS from the advertisement data.
     *
     * On Android, this field will never be populated for apps targeting Android SDK 31+
     * unless the app has obtained BLUETOOTH_CONNECT permission,
     * as that permission is a new requirement from Android to read this field.
     *
     * On iOS, this is always null.
     */
     val bluetoothName: String?,
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



